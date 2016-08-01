package com.rake.rakeapi.tools.queue.command;

import com.rake.rakeapi.tools.queue.QueueReader;
import com.rake.rakeapi.tools.queue.QueueState;
import jline.console.completer.ArgumentCompleter;
import jline.console.completer.Completer;
import jline.console.completer.NullCompleter;
import jline.console.completer.StringsCompleter;

import java.util.Iterator;

public class Help extends Command{
    @Override
    public Completer getCompleter() {
        return new ArgumentCompleter(new StringsCompleter(getCommand()), new NullCompleter());
    }

    @Override
    public String getDescription() {
        return "print this help messages";
    }

    @Override
    public String getUsage() {
        return "help";
    }

    @Override
    public String process(Iterator<String> arguments, QueueReader queueReader, QueueState queueState) {
        StringBuilder sb = new StringBuilder();
        for(Command command : queueReader.getCommandMap().values()) {
            sb.append(String.format("\n  %-30s - %s", command.getUsage(), command.getDescription()));
        }
        sb.append("\n");
        return sb.toString();
    }
}
