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
 * Created by jl on 2/12/15.
 */
public class Close extends Command {
    @Override
    public Completer getCompleter() {
        return new ArgumentCompleter(new StringsCompleter("close"), new NullCompleter());
    }

    @Override
    public String getDescription() {
        return "close queue which you opended";
    }

    @Override
    public String process(Iterator<String> arguments, QueueReader queueReader, QueueState queueState) {
        FileBlockingQueue<String> fileQueue = queueState.getFileBlockingQueue();
        if (null != fileQueue) {
            fileQueue.close();
            queueState.setPrompt("");
            queueState.setOpened(false);
            return "Queue [" + queueState.getQueueDir() + "] is closed";
        } else {
            return "No queue is opened";
        }
    }
}
