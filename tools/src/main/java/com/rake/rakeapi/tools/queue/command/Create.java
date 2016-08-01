package com.rake.rakeapi.tools.queue.command;

import com.rake.rakeapi.queue.FileBlockingQueue;
import com.rake.rakeapi.queue.StringSerDe;
import com.rake.rakeapi.tools.queue.QueueReader;
import com.rake.rakeapi.tools.queue.QueueState;
import jline.console.completer.*;

import java.io.File;
import java.util.Iterator;

public class Create extends Command {
    @Override
    public Completer getCompleter() {
        return new ArgumentCompleter(new StringsCompleter(getCommand()), new FileNameCompleter(), new NullCompleter());
    }

    @Override
    public String getUsage() {
        return getCommand() + " base_directory";
    }

    @Override
    public String getDescription() {
        return "create queue";
    }

    @Override
    public String process(Iterator<String> arguments, QueueReader queueReader, QueueState queueState) {
        if (!arguments.hasNext()) {
            return "base_directory doesn't set";
        } else if (queueState.isOpened()) {
            return "Queue already opened";
        } else {
            File dir = new File(arguments.next());
            if(dir.exists()){
                return "path already exists";
            }
            String queuePath = dir.getParent();
            String queueName = dir.getName();

            FileBlockingQueue<String> fileBlockingQueue;
            try {
                fileBlockingQueue = new FileBlockingQueue<String>(queuePath, queueName, 60, new StringSerDe(), true);
            } catch (Exception e) {
                return "Cannot create queue\n" +
                        "queuePath = [" + queuePath + "]\n" +
                        "queueName = [" + queueName + "]\n" + e.getMessage();
            }
            queueState.setFileBlockingQueue(fileBlockingQueue);
            queueState.setPrompt(dir.getAbsolutePath());
            queueState.setOpened(true);
            return "Queue [" + dir.getAbsolutePath() + "] created";
        }
    }
}
