package com.rake.rakeapi.tools.queue.command;

import com.rake.rakeapi.queue.FileBlockingQueue;
import com.rake.rakeapi.tools.queue.QueueReader;
import com.rake.rakeapi.tools.queue.QueueState;
import jline.console.completer.ArgumentCompleter;
import jline.console.completer.Completer;
import jline.console.completer.NullCompleter;
import jline.console.completer.StringsCompleter;

import java.util.Iterator;

/**
 * Created by jl on 2/13/15.
 */
public class Read extends Command {
    @Override
    public Completer getCompleter() {
        return new ArgumentCompleter(new StringsCompleter("read"), new NullCompleter());
    }

    @Override
    public String getDescription() {
        return "read one line from queue";
    }

    @Override
    public String process(Iterator<String> arguments, QueueReader queueReader, QueueState queueState) {
        if (!queueState.isOpened()) {
            return "Open first";
        } else {
            synchronized (queueReader) {
                FileBlockingQueue<String> fileBlockingQueue = queueState.getFileBlockingQueue();
                String line = fileBlockingQueue.poll();
                if(null == line) {
                    return "no data exists in queue";
                }
                try {
                    fileBlockingQueue.put(line);
                } catch (InterruptedException e) {
                    queueReader.printMessage(String.format("Cannot add [%s] again", line));
                }
                return line;
            }
        }
    }
}
