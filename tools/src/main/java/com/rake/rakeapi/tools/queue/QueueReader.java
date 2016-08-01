package com.rake.rakeapi.tools.queue;

import com.google.common.base.Splitter;
import com.rake.rakeapi.tools.queue.command.*;
import jline.console.ConsoleReader;
import jline.console.completer.AggregateCompleter;
import jline.console.completer.Completer;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

public class QueueReader {
    private final String newLine = System.getProperty("line.separator", "\n");
    private final String delim = " ";
    private ConsoleReader consoleReader;
    private QueueState queueState;
    private Boolean isRunning;


    private Map<String, Command> commandMap;
    private List<Completer> completers;

    public QueueReader() throws IOException {
        this(new ConsoleReader());
    }

    public QueueReader(ConsoleReader consoleReader) {
        setConsoleReader(consoleReader);
        initialize();
    }

    private void initialize() {
        isRunning = true;
        queueState = new QueueState();
        commandMap = new HashMap<String, Command>();

        completers = new ArrayList<Completer>();

        for (Command command : new Command[]{new Help(), new Close(), new Read(), new Open(), new Create(), new Write(), new Quit(), new Save()}) {
            commandMap.put(command.getCommand(), command);
            completers.add(command.getCompleter());
        }

        consoleReader.setPrompt(queueState.getAllPrompt());

        consoleReader.addCompleter(
                new AggregateCompleter(
                        completers
                /*
                        *//* help *//*
                        new ArgumentCompleter(new StringsCompleter("help"), new NullCompleter()),
                        *//* open {queue directory} *//*
                        new ArgumentCompleter(new StringsCompleter("open"), new FileNameCompleter(), new NullCompleter()),
                        *//* close *//*
                        new ArgumentCompleter(new StringsCompleter("close"), new NullCompleter()),
                        *//* read *//*
                        new ArgumentCompleter(new StringsCompleter("read"), new NullCompleter()),
                        *//* save *//*
                        new ArgumentCompleter(new StringsCompleter("save"), new FileNameCompleter(), new NullCompleter()),
                        *//* save_direct *//*
                        new ArgumentCompleter(new StringsCompleter("save_direct"), new FileNameCompleter(), new FileNameCompleter(), new NullCompleter()),
                        *//* Only for developer *//*
                        *//* create *//*
                        new ArgumentCompleter(new StringsCompleter("create"), new FileNameCompleter(), new NullCompleter()),
                        *//* write *//*
                        new ArgumentCompleter(new StringsCompleter("write"), new NullCompleter())*/
                )
        );
    }

    private void commandLoop() throws IOException {
        String line;
        while (isRunning && null != (line = consoleReader.readLine())) {
            printMessage(process(line));
            consoleReader.setPrompt(queueState.getAllPrompt());
        }
    }

    public void printMessage(String line){
        PrintWriter printWriter;
        printWriter = new PrintWriter(consoleReader.getOutput());
        printWriter.write(line);
        printWriter.write(newLine);
        printWriter.flush();
    }

    private String process(String line) {
        Iterator<String> arguments = Splitter.on(delim).split(line).iterator();
        Command command;
        if (!arguments.hasNext() || null == (command = commandMap.get(arguments.next()))) {
            command = new Help();
        }
        return command.process(arguments, this, queueState);
    }

    public String commandHelp() {
        String helpMessage =
                "help                                         - print this help messages" +
                        "open queue_directory                         - open queue" +
                        "close                                        - close queue which you opended" +
                        "read                                         - read one line from queue" +
                        "save base_directory                          - save queue to base_directory for sending them manually" +
                        "save_direct queue_directory base_directory   - save queue to base_directory without opening queue";
        return helpMessage;
    }

    public Map<String, Command> getCommandMap() {
        return commandMap;
    }

    public void setConsoleReader(ConsoleReader consoleReader) {
        this.consoleReader = consoleReader;
    }

    public static void main(String[] args) throws IOException {
        new QueueReader().commandLoop();
    }

    public void setRunning(boolean running) {
        this.isRunning = running;
    }
}
