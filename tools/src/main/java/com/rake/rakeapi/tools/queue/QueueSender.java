package com.rake.rakeapi.tools.queue;

import com.rake.rakeapi.Sender;
import com.rake.rakeapi.queue.MemoryQueue;
import com.rake.rakeapi.sender.AsyncKafkaSender;
import com.rake.rakeapi.sender.ConsoleSender;
import com.rake.rakeapi.util.RakeProperties;
import org.apache.commons.cli.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Properties;

public class QueueSender {
    private final Logger logger = LoggerFactory.getLogger(QueueSender.class);
    private Options options;
    private Sender sender;

    public QueueSender() {
        options = new Options();
        Option brokerList = OptionBuilder
                .withArgName("ip:port[,ip:port[,...]]")
                .withLongOpt("broker-list")
                .hasArg()
                .withDescription("Server list to data send")
                .isRequired()
                .create();
        Option dryRun = OptionBuilder
                .withLongOpt("dry-run")
                .withDescription("Only print logs into the console if you use file")
                .create();
        Option file = OptionBuilder
                .withArgName("/path/to/file")
                .withLongOpt("file")
                .hasArg()
                .withDescription("File stored queue")
                .isRequired()
                .create();
        options.addOption(brokerList);
        options.addOption(dryRun);
        options.addOption(file);
    }

    public void process(String[] args) throws Exception {
        logger.info("QueueSender started");
        try {
            Parser parser = new BasicParser();
            CommandLine commandLineOptions = parser.parse(options, args);
            long startTime = System.currentTimeMillis();
            long endTime;
            boolean dryRun = commandLineOptions.hasOption("dry-run");

            File file = null;
            if (commandLineOptions.hasOption("file")) {
                file = new File(commandLineOptions.getOptionValue("file"));
                if (!file.isFile())
                    throw new FileNotFoundException(String.format("Wrong file: [%s]", file.getAbsoluteFile()));
            }
            String brokerList = commandLineOptions.getOptionValue("broker-list");
            if (!brokerList.matches("([A-Za-z0-9\\-\\.]*:[0-9]*,)*[A-Za-z0-9\\-\\.]*:[0-9]*"))
                throw new Exception(String.format("broker-list doesn't have correct format: [%s]", brokerList));

            Properties properties = new Properties();
            properties.setProperty("api.type", "live");
            properties.setProperty("logger.type", "sync");
            if (dryRun) {
                logger.info("dry-run");
                setSender(new ConsoleSender(new MemoryQueue(new RakeProperties(properties)), new RakeProperties(properties)));
            } else {
                logger.warn("Send to Kafka");
                properties.put("async.kafka.bootstrap.servers", brokerList);
                setSender(new AsyncKafkaSender(new MemoryQueue(new RakeProperties(properties)), new RakeProperties(properties)));
            }
            BufferedReader in = new BufferedReader(new FileReader(file));
            int count = 0;

            while (in.ready()) {
                String line = in.readLine();
                if (!sender.send(line))
                    logger.info("Cannot send: [{}]", line);
                count++;
                if (0 == (count % 100)) System.out.print(".");
            }
            logger.info("Enqueue Finish ");
            logger.info("Sender size  is :"+sender);

            int countt = 0;
            while(true){
                logger.info("Waiting Send ALL ");
                Thread.sleep(100000);
                if(++countt==100000){
                    break;
                }
            }


            endTime = System.currentTimeMillis() - startTime;

            in.close();


            logger.info("Finished sending: " + file);
            logger.info("The Time Required : "+endTime);
        } catch (ParseException e) {
            printHelp();
            throw e;
        }
    }

    public void printHelp() {
        HelpFormatter formatter = new HelpFormatter();
        formatter.setWidth(200);
        formatter.printHelp("queue-sender.sh", options);

    }

    public static void main(String[] args) {
        try {
            new QueueSender().process(args);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void setSender(Sender sender) {
        this.sender = sender;
    }
}
