package com.rake.rakeapi.tools.queue.command;

import com.rake.rakeapi.tools.queue.QueueReader;
import com.rake.rakeapi.tools.queue.QueueState;
import jline.console.completer.Completer;

import java.util.Iterator;

public abstract class Command {
    public abstract Completer getCompleter();

    public String getUsage() {
        return getCommand();
    }

    public abstract String getDescription();

    public String getCommand() {
        return this.getClass().getSimpleName().toLowerCase();
    }

    public abstract String process(Iterator<String> arguments, QueueReader queueReader, QueueState queueState);
}
