package com.rake.rakeapi.tools.queue;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import org.apache.commons.cli.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Iterator;

/**
 * Created by KBS on 2016. 5. 30..
 */
public class FileTokenizer {
    private final Logger logger = LoggerFactory.getLogger(FileTokenizer.class);
    private Options options;
    private final static String delim = "\"}{\"";
    private final static String appendDelim = "\"}\n{\"";
    private final static String lastDelim = "\n";
    private FileWriter fileWriter;

    public FileTokenizer() {
        options = new Options();
        Option filePath = OptionBuilder
                .withArgName("/path/to/file")
                .withLongOpt("origin")
                .hasArg()
                .withDescription("Origin File or Directory path")
                .isRequired()
                .create();
        Option targetPath = OptionBuilder
                .withArgName("/path/to/file")
                .withLongOpt("target")
                .hasArg()
                .withDescription("Target File path")
                .isRequired()
                .create();
        options.addOption(filePath);
        options.addOption(targetPath);
    }

    public void openFileWriter(File targetFile){
        try {
            fileWriter = new FileWriter(targetFile);
        }catch(IOException e){
            e.printStackTrace();
            logger.error("File Writer IO Exception : " + e);
        }
    }

    public File getOption(String[] args) {
        try {
            logger.info("Args size is :" + args.length);
            Parser parser = new BasicParser();
            CommandLine commandLineOptions = parser.parse(options, args);
            File originFile = null;
            File targetFile = null;

            for (int i = 0; i < args.length; i++) {
                logger.info("args" + args[i]);
            }

            if (commandLineOptions.hasOption("origin")) {
                originFile = new File(commandLineOptions.getOptionValue("origin"));
                logger.info("Origin file path is right");
                if (!originFile.isFile() && !originFile.isDirectory())
                    throw new FileNotFoundException(String.format("File or Directory is not exist : [%s]", originFile.getAbsoluteFile()));
            }

            if (commandLineOptions.hasOption("target")) {
                targetFile = new File(commandLineOptions.getOptionValue("target"));
                if (!targetFile.exists()) {
                    targetFile.createNewFile();
                    logger.info("Target file is created");
                } else if (targetFile.exists() || !targetFile.isFile()) {
                    logger.info("Target file already exist");
                    throw new FileNotFoundException(String.format("Wrong file: [%s]", targetFile.getAbsoluteFile()));
                }
            }

            openFileWriter(targetFile);
            return originFile;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            logger.error("File Not Found Exception : " + e);
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("Create New File IO Exception : " + e);
            return null;
        } catch (ParseException e) {
            e.printStackTrace();
            logger.error("Parse Exception : " + e);
            return null;
        }
    }

    public void process(File originFile) {
        try {
            BufferedReader in = new BufferedReader(new FileReader(originFile));
            int count = 0;
            while (in.ready()) {
                String line = in.readLine();
                Iterator<String> sizeCheck = Splitter.on(delim).split(line).iterator();
                int size = Lists.newArrayList(sizeCheck).size();
                Iterator<String> parts = Splitter.on(delim).split(line).iterator();

                logger.info("Tokenizer array size is : " + size);
                while (parts.hasNext()) {
                    if (count++ == size - 1) {
                        fileWriter.append(parts.next());
                        fileWriter.append(lastDelim);
                        fileWriter.flush();
                        break;
                    } else {
                        fileWriter.append(parts.next());
                        fileWriter.append(appendDelim);
                        fileWriter.flush();
                    }
                    if (0 == (count % 100)) System.out.print(".");
                }
            }
            in.close();

            System.out.println();
            logger.info("Finished sending: " + originFile);
            System.out.println("Finished sending: " + originFile);


        } catch (Exception e) {

        }
    }

    public void getFilesInDirectory(File f) {
        if (f.isDirectory()) {
            String[] list = f.list();
            for (int i = 0; i < list.length; i++) {
                getFilesInDirectory(new File(f, list[i]));
            }
        } else {
            logger.info("f is " + f.getAbsolutePath());
            System.out.println("f is " + f.getAbsolutePath());

            process(f);
        }
    }

    public void close() {
        try {
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String args[]) {
        FileTokenizer ft = new FileTokenizer();
        ft.getFilesInDirectory(ft.getOption(args));
        ft.close();
    }
}
