package com.rake.rakeapi.tools.queue.command;

import com.rake.rakeapi.queue.FileBlockingQueue;
import com.rake.rakeapi.tools.queue.QueueReader;
import com.rake.rakeapi.tools.queue.QueueState;
import jline.console.completer.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Save extends Command {
    @Override
    public Completer getCompleter() {
        return new ArgumentCompleter(new StringsCompleter("save"), new FileNameCompleter(), new NullCompleter());
    }

    @Override
    public String getDescription() {
        return "save queue to base_directory for sending them manually";
    }

    @Override
    public String process(Iterator<String> arguments, QueueReader queueReader, QueueState queueState) {
        if (!queueState.isOpened()) {
            return "No queue to save";
        }
        if (!arguments.hasNext()) {
            return "No path for saving queue";
        }
        File baseDir = new File(arguments.next());

        if (!baseDir.exists()) {
            baseDir.mkdirs();
            queueReader.printMessage("Created " + baseDir.getAbsolutePath());
        }

        if (!baseDir.isDirectory())
            return "That path is not a directory";
        File file = new File(baseDir, new File(queueState.getQueueDir()).getName());
        FileBlockingQueue<String> fileBlockingQueue = queueState.getFileBlockingQueue();
        queueReader.printMessage(fileBlockingQueue.size() + " will be saved to " + file.getAbsolutePath());
        FileWriter fileWriter;
        try {
            fileWriter = new FileWriter(file);
        } catch (IOException e) {
            return "File open error";
        }

        List<String> tempQueue = new ArrayList<String>();
        String line = null;
        try {
            while (null != (line = fileBlockingQueue.poll())) {
                tempQueue.add(line);
                fileWriter.write(line);
                fileWriter.write("\n");
                fileWriter.flush();
            }
            fileWriter.close();
        } catch (IOException e) {
            queueReader.printMessage("Error occurs while saving log into file. REVERT!!");
            for (String backLine : tempQueue) {
                try {
                    fileBlockingQueue.put(backLine);
                } catch (InterruptedException e1) {
                    queueReader.printMessage("Error occurs while reverting... save the below line!!\n" + backLine);
                }
            }
            try {
                fileWriter.close();
            } catch (IOException e1) {
                // Do nothing
            } finally {
                file.delete();
            }
            return "Failed to save";
        }

        return "saved : " + file.getAbsolutePath();
    }
}
