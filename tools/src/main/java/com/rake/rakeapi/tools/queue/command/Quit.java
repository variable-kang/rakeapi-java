package com.rake.rakeapi.tools.queue.command;

import com.rake.rakeapi.tools.queue.QueueReader;
import com.rake.rakeapi.tools.queue.QueueState;
import jline.console.completer.ArgumentCompleter;
import jline.console.completer.Completer;
import jline.console.completer.NullCompleter;
import jline.console.completer.StringsCompleter;

import java.util.Iterator;

public class Quit extends Command {
    @Override
    public Completer getCompleter() {
        return new ArgumentCompleter(new StringsCompleter(getCommand()), new NullCompleter());
    }

    @Override
    public String getDescription() {
        return "quite shell";
    }

    @Override
    public String process(Iterator<String> arguments, QueueReader queueReader, QueueState queueState) {
        if(queueState.isOpened())
        {
            queueReader.printMessage("closing queue");
            queueState.getFileBlockingQueue().close();
            queueReader.printMessage("queue closed");
            queueState.setOpened(false);
            queueState.setPrompt("");
            queueState.setQueueDir("");
        }
        queueReader.setRunning(false);
        return "quiting...";
    }
}
