package com.openclassrooms.concurrency.planetbrain.atomic.app;

import com.openclassrooms.concurrency.planetbrain.atomic.service.AtomicBasedFileAnalyzer;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

public class AtomicBasedPlanetAnalyzerApp {
    private static AtomicBasedFileAnalyzer fileAnalyzer = new AtomicBasedFileAnalyzer();

    public static void main(String[] files) throws InterruptedException, ExecutionException, URISyntaxException, IOException {
        Double average = getAverageOfTemperatureFiles(files);
        System.out.println("Calculated a mean of " + average);
    }

    private static Double getAverageOfTemperatureFiles(String[] files) throws InterruptedException, ExecutionException, URISyntaxException {
        // Get file paths
        String fileOne = files[0];
        String fileTwo = files[1];
        Path fileOnePath = getCSVPath(fileOne);
        Path fileTwoPath = getCSVPath(fileTwo);

        Double average;
        ExecutorService executorService = null;
        try {
            // 1. ThreadPool
            executorService = Executors.newFixedThreadPool(4);

            // 2. Submit Each Future for summing
            Future futureOfFileOne = executorService.submit(() -> fileAnalyzer.processFile(fileOnePath));
            Future futureOfFileTwo = executorService.submit(() -> fileAnalyzer.processFile(fileTwoPath));

            // 3. Wait for Futures
            futureOfFileOne.get();
            futureOfFileTwo.get();

            // calcualte average from atomics
            average = fileAnalyzer.getTemperatureSum() / fileAnalyzer.getSampleSize();
        } finally {
            if (null != executorService) {
                executorService.shutdown();
            }
        }
        return average;
    }

    private static Path getCSVPath(String file) throws URISyntaxException {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        URL resource = loader.getResource(file);
        if (resource == null) {
            throw new RuntimeException("Bad file name provided");
        }
        return Paths.get(resource.toURI());
    }
}
