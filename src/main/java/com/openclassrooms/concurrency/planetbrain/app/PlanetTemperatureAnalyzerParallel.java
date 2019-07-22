package com.openclassrooms.concurrency.planetbrain.app;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class PlanetTemperatureAnalyzerParallel {

    private static Logger LOGGER = Logger.getLogger(PlanetTemperatureAnalyzerParallel.class.getName());
    public static void main(String[] args) throws Exception{


        // Create a Process builder and make it look like this process
        ProcessBuilder builder = new ProcessBuilder();

        // Make sure we can see the errors and output of our process
        builder.inheritIO();

        // Share all environment variables
        builder.environment().putAll(System.getenv());

        // Grab our current class path and set it.
        String classPathString = System.getProperty("java.class.path");


        // Start a process with each fileName asynchronously
        List<Process> processes = Arrays.asList(args).stream().map(csvFile -> {

            builder.command("java", "-cp", classPathString, PlanetTemperatureAnalyzer.class.getName(), csvFile);
            Process process;
            try {
                process = builder.start();
                LOGGER.info("Started" + process.info());
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
            LOGGER.info("Completed child process");
            return process;
        }).collect(Collectors.toList());

        processes.forEach(process -> {
            try {
                LOGGER.info("Waiting for process to complete. PID[" + process.pid() + "]");
                process.waitFor();
                LOGGER.info("Process completed. PID[" + process.pid() + "]");
            } catch (InterruptedException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        });

    }
}