package com.rake.rakeapi.tools.queue.command;

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
public class Write extends Command {
    @Override
    public Completer getCompleter() {
        return new ArgumentCompleter(new StringsCompleter(getCommand()), new NullCompleter());
    }

    @Override
    public String getUsage() {
        return getCommand() + " data";
    }

    @Override
    public String getDescription() {
        return "write data into queue";
    }

    @Override
    public String process(Iterator<String> arguments, QueueReader queueReader, QueueState queueState) {
        if (!queueState.isOpened()) {
            return "No queue to write";
        } else if (!arguments.hasNext()) {
            return "no data to write";
        } else {
            String line = arguments.next();
            try {
                queueState.getFileBlockingQueue().put(line);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "[" + line + "] added";
        }
    }
}
