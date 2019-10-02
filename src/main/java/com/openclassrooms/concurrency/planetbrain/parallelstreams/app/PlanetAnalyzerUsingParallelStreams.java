package com.openclassrooms.concurrency.planetbrain.parallelstreams.app;

import com.openclassrooms.concurrency.planetbrain.parallelstreams.service.ParallelStreamingAnalyzer;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.logging.Logger;

/**
 * Uses parallel streams to read a kepler file and report on the average temperature
 * in that file.
 */
public class PlanetAnalyzerUsingParallelStreams {
    private final static Logger LOGGER = Logger.getLogger(PlanetAnalyzerUsingParallelStreams.class.getName());

    public static void main(String[] args) throws Exception {
        String file = args[0];
        Double average = analyzeFile(file);
        LOGGER.info("File["+file+"] Average planetary temperature: " + average + " Kelvin");
    }

    /**
     * Calculates the average temperature in a kepler file
     * @param file with kepler data
     * @return mean temperature in the file
     */
    public static Double analyzeFile(String file)  {
        Double average;
        try {
            URI fileURI = getCsvURI(file);
            final ParallelStreamingAnalyzer streamingAnalyzer = new ParallelStreamingAnalyzer();
            average = streamingAnalyzer.calculateAverageFor(fileURI);
        } catch (IOException|URISyntaxException e) {
            e.printStackTrace();
            LOGGER.severe(e.getMessage());
            throw new RuntimeException(e);
        }
        return average;
    }

    private static URI getCsvURI(String file) throws URISyntaxException {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        URL resource = loader.getResource(file);
        if (resource == null) {
            throw new RuntimeException("Bad file name provided");
        }
        return resource.toURI();
    }
}