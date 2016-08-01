package com.rake.rakeapi.queue;

import com.rake.rakeapi.Queue;
import com.rake.rakeapi.util.RakeProperties;

import java.io.IOException;

/**
 * Created by jl on 4/21/14.
 */
public class FileQueue extends Queue {
    private final FileBlockingQueue<String> fileBlockingQueue;

    public FileQueue(RakeProperties rakeProperties) throws IOException {
        super(rakeProperties);

        // For support on old distribution
        String queueFileName = rakeProperties.get("queue.file.name");
        fileBlockingQueue = new FileBlockingQueue<String>(
                rakeProperties.get("queue.file.path") + rakeProperties.get("service.id"),
                queueFileName,
                Integer.valueOf(rakeProperties.get("queue.file.gc_period_sec")),
                new StringSerDe(),
                true
        );
    }

    @Override
    public Boolean enQueue(String log) {
        return fileBlockingQueue.add(log);
    }

    @Override
    public String deQueue() {
        return fileBlockingQueue.poll();
    }

    @Override
    public int size() {
        return fileBlockingQueue.size();
    }

    @Override
    public void close() {
        fileBlockingQueue.close();
    }
}
