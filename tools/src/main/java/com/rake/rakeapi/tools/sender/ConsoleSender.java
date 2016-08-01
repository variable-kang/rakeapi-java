package com.rake.rakeapi.tools.sender;

import com.rake.rakeapi.LoggerAPI;
import org.apache.commons.cli.*;

import java.io.Console;
import java.util.Properties;

/**
 * Created by jl on 7/7/15.
 */
public class ConsoleSender {
    private Options options;

    public ConsoleSender() {
        options = new Options();
        Option apiType = OptionBuilder
                .withArgName("{dev,proto}")
                .withLongOpt("api-type")
                .hasArg()
                .withDescription("Type of LoggerAPI")
                .isRequired()
                .create();
        Option serviceId = OptionBuilder
                .withArgName("service_id")
                .withLongOpt("service-id")
                .hasArg()
                .withDescription("Service ID for sending")
                .isRequired()
                .create();
        options.addOption(apiType);
        options.addOption(serviceId);
    }

    public static void main(String[] args) {
        new ConsoleSender().run(args);
    }

    private void run(String[] args) {
        Parser parser = new BasicParser();
        CommandLine commandLineOptions = null;
        try {
            commandLineOptions = parser.parse(options, args);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        Properties properties = new Properties();
        properties.put("api.type", commandLineOptions.getOptionValue("api-type"));
        properties.put("service.id", "rakeapi-tester");

        LoggerAPI loggerAPI = null;
        try {
            loggerAPI = LoggerAPI.getInstance(properties);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        Console console = System.console();
        if (null == console) {
            throw new RuntimeException("No console");
        }

        String serviceId = commandLineOptions.getOptionValue("service-id");

        String line;
        while (null != (line = console.readLine())) {
            loggerAPI.log(serviceId, line);
        }
        
        /*RakeProperties rakeProperties = new RakeProperties(properties);

        KafkaSender kafkaSender = null;
        try {
            kafkaSender = new KafkaSender(rakeProperties);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        Scanner consoleScanner = new Scanner(System.in);
        Map<String, String> jsonMap = new HashMap<>();
        jsonMap.put("service_id", commandLineOptions.getOptionValue("service-id"));
        while (consoleScanner.hasNext()) {
            jsonMap.put("log", consoleScanner.next());            
        }*/
    }
}
