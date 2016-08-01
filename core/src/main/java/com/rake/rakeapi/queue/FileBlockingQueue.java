package com.rake.rakeapi.queue;

import com.google.common.base.Preconditions;
import com.leansoft.bigqueue.BigArrayImpl;
import com.leansoft.bigqueue.IBigArray;
import com.leansoft.bigqueue.page.IMappedPage;
import com.leansoft.bigqueue.page.IMappedPageFactory;
import com.leansoft.bigqueue.page.MappedPageFactoryImpl;
import com.rake.rakeapi.ErrorLog;
import com.rake.rakeapi.ThreadFactoryFactory;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.AbstractQueue;
import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * File based blocking queue with <a href="https://github.com/bulldog2011/bigqueue">BigQueue</a> and Netflix/Suro
 *
 * @param <E> Type name should be given and its SerDe should be implemented.
 *            <p/>
 *            With the argument path and name, files will be created under the directory
 *            [path]/[name]. BigQueue needs to do garge collection, which is deleting
 *            unnecessary page file. Garbage collection is done in the background every
 *            gcPeriodInSec seconds.
 *            <p/>
 *            When the messages are retrieved from the queue,
 *            we can control the behavior whether to remove messages immediately or wait
 *            until we commit. autoCommit true means removing messages immediately.
 * @author jbae, improved by jl
 */
public class FileBlockingQueue<E> extends AbstractQueue<E> implements BlockingQueue<E> {
    // 2 ^ 3 = 8
    final static int QUEUE_FRONT_INDEX_ITEM_LENGTH_BITS = 3;
    // size in bytes of queue front index page
    final static int QUEUE_FRONT_INDEX_PAGE_SIZE = 1 << QUEUE_FRONT_INDEX_ITEM_LENGTH_BITS;
    // only use the first page
    static final long QUEUE_FRONT_PAGE_INDEX = 0;
    // folder name for queue front index page
    final static String QUEUE_FRONT_INDEX_PAGE_FOLDER = "front_index";
    final IBigArray innerArray;
    // front index of the big queue,
    final AtomicLong queueFrontIndex = new AtomicLong();
    private final Lock lock = new ReentrantLock();
    private final Condition notEmpty = lock.newCondition();
    private final SerDe<E> serDe;
    private final boolean autoCommit;
    // factory for queue front index page management(acquire, release, cache)
    IMappedPageFactory queueFrontIndexPageFactory;
    private long consumedIndex;

    private ScheduledExecutorService gcExecutorService;
    private final int gcPeriodInSec;
    private ErrorLog errorLog = ErrorLog.getInstance();
    private final Logger logger = LoggerFactory.getLogger(FileBlockingQueue.class);

    public FileBlockingQueue(
            String path,
            final String name,
            int gcPeriodInSec,
            SerDe<E> serDe,
            boolean autoCommit) throws IOException {
        innerArray = new BigArrayImpl(path, name);
        // the ttl does not matter here since queue front index page is always cached
        this.queueFrontIndexPageFactory = new MappedPageFactoryImpl(QUEUE_FRONT_INDEX_PAGE_SIZE,
                ((BigArrayImpl) innerArray).getArrayDirectory() + QUEUE_FRONT_INDEX_PAGE_FOLDER,
                10 * 1000/*does not matter*/);
        IMappedPage queueFrontIndexPage = this.queueFrontIndexPageFactory.acquirePage(QUEUE_FRONT_PAGE_INDEX);

        ByteBuffer queueFrontIndexBuffer = queueFrontIndexPage.getLocal(0);
        long front = queueFrontIndexBuffer.getLong();
        queueFrontIndex.set(front);

        consumedIndex = front;

        this.gcPeriodInSec = gcPeriodInSec;

        gcExecutorService = Executors.newScheduledThreadPool(1, new ThreadFactoryFactory(new ThreadGroup("file-queue"), "gc"));

        gcExecutorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                try {
                    gc();
                } catch (IOException e) {
                    errorLog.sendError("IOException while gc", e);
                }

            }
        }, gcPeriodInSec, gcPeriodInSec, TimeUnit.SECONDS);

        this.serDe = serDe;
        this.autoCommit = autoCommit;
    }

    private synchronized void gc() throws IOException {
        logger.info("Start FileBlockingQueue gc");
        long beforeIndex = this.queueFrontIndex.get();
        if (beforeIndex > 0L) { // wrap
            beforeIndex--;
            try {
                logger.debug(String.format("gc index : [%d]", beforeIndex));
                this.innerArray.removeBeforeIndex(beforeIndex);
            } catch (IndexOutOfBoundsException e) {
                errorLog.error("Exception on gc: " + e.getMessage(), e);
            }
        }
        logger.info("Finish FileBlockingQueue gc");
    }

    public void commit() {
        try {
            lock.lock();
            commitInternal(true);
        } catch (IOException e) {
            errorLog.error("IOException on commit: " + e.getMessage(), e);
        } finally {
            lock.unlock();
        }
    }

    private void commitInternal(boolean doCommit) throws IOException {
        if (!doCommit) return;

        this.queueFrontIndex.set(consumedIndex);
        // persist the queue front
        IMappedPage queueFrontIndexPage = null;
        queueFrontIndexPage = this.queueFrontIndexPageFactory.acquirePage(QUEUE_FRONT_PAGE_INDEX);
        ByteBuffer queueFrontIndexBuffer = queueFrontIndexPage.getLocal(0);
        queueFrontIndexBuffer.putLong(consumedIndex);
        queueFrontIndexPage.setDirty(true);
    }

    public void close() {
        try {
            if (null != gcExecutorService && !gcExecutorService.isShutdown()) {
                logger.info("Shutdown a gc thread of fileblockingqueue");
                gcExecutorService.shutdown();
                gcExecutorService = null;
            }

            logger.info("Cleanup fileblockingqueue...");

            gc();
            if (this.queueFrontIndexPageFactory != null) {
                this.queueFrontIndexPageFactory.releaseCachedPages();
            }

            this.innerArray.close();
        } catch (IOException e) {
            errorLog.error("IOException while closing: " + e.getMessage(), e);
        }
    }

    @Override
    public E poll() {
        if (isEmpty()) {
            return null;
        }
        E x = null;
        lock.lock();
        try {
            if (!isEmpty()) {
                x = consumeElement();
                if (!isEmpty()) {
                    notEmpty.signal();
                }
            }
        } catch (IOException e) {
            errorLog.error("IOException while poll: " + e.getMessage(), e);
            return null;
        } finally {
            lock.unlock();
        }

        return x;
    }

    @Override
    public E peek() {
        if (isEmpty()) {
            return null;
        }
        lock.lock();
        try {
            return consumeElement();
        } catch (IOException e) {
            errorLog.error("IOException while peek: " + e.getMessage(), e);
            return null;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean offer(E e) {
        Preconditions.checkNotNull(e);
        try {
            innerArray.append(serDe.serialize(e));
            signalNotEmpty();
            return true;
        } catch (IOException ex) {
            return false;
        }
    }

    @Override
    public void put(E e) throws InterruptedException {
        offer(e);
    }

    @Override
    public boolean offer(E e, long timeout, TimeUnit unit) throws InterruptedException {
        return offer(e);
    }

    @Override
    public E take() throws InterruptedException {
        E x;
        lock.lockInterruptibly();
        try {
            while (isEmpty()) {
                notEmpty.await();
            }
            x = consumeElement();
        } catch (IOException e) {
            errorLog.error("IOException on take: " + e.getMessage(), e);
            return null;
        } finally {
            lock.unlock();
        }

        return x;
    }

    private E consumeElement() throws IOException {
        // restore consumedIndex if not committed
        consumedIndex = this.queueFrontIndex.get();
        E x = serDe.deserialize(innerArray.get(consumedIndex));
        ++consumedIndex;
        commitInternal(autoCommit);
        return x;
    }

    @Override
    public E poll(long timeout, TimeUnit unit) throws InterruptedException {
        E x = null;
        long nanos = unit.toNanos(timeout);
        lock.lockInterruptibly();
        try {
            while (isEmpty()) {
                if (nanos <= 0)
                    return null;
                nanos = notEmpty.awaitNanos(nanos);
            }
            x = consumeElement();
        } catch (IOException e) {
            errorLog.error("IOException on poll: " + e.getMessage(), e);
            return null;
        } finally {
            lock.unlock();
        }

        return x;
    }

    @Override
    public int remainingCapacity() {
        return Integer.MAX_VALUE;
    }

    @Override
    public int drainTo(Collection<? super E> c) {
        return drainTo(c, Integer.MAX_VALUE);
    }

    @Override
    public int drainTo(Collection<? super E> c, int maxElements) {
        if (c == null)
            throw new NullPointerException();
        if (c == this)
            throw new IllegalArgumentException();

        lock.lock();
        // restore consumedIndex if not committed
        consumedIndex = this.queueFrontIndex.get();
        try {
            int n = Math.min(maxElements, size());
            // count.get provides visibility to first n Nodes
            int i = 0;
            while (i < n && consumedIndex < innerArray.getHeadIndex()) {
                c.add(serDe.deserialize(innerArray.get(consumedIndex)));
                ++consumedIndex;
                ++i;
            }
            commitInternal(autoCommit);
            return n;
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public Iterator<E> iterator() {
        return new Iterator<E>() {

            @Override
            public boolean hasNext() {
                return !isEmpty();
            }

            @Override
            public E next() {
                try {
                    E x = consumeElement();
                    return x;
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException("remove is not supported, use dequeue()");
            }
        };
    }

    @Override
    public int size() {
        return (int) (innerArray.getHeadIndex() - queueFrontIndex.get());
    }

    private void signalNotEmpty() {
        lock.lock();
        try {
            notEmpty.signal();
        } finally {
            lock.unlock();
        }
    }
}

