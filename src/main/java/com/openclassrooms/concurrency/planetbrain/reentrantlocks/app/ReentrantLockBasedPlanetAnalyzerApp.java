package com.openclassrooms.concurrency.planetbrain.reentrantlocks.app;

import com.openclassrooms.concurrency.planetbrain.reentrantlocks.model.ThreadSafePlanetSampler;
import com.openclassrooms.concurrency.planetbrain.reentrantlocks.service.ThreadSafePlanetFileAnalyzer;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.locks.ReentrantLock;

public class ReentrantLockBasedPlanetAnalyzerApp {

    private ThreadSafePlanetSampler sampler;
    private ThreadSafePlanetFileAnalyzer fileAnalyzer;

    public static void main(String[] files) throws InterruptedException, ExecutionException, URISyntaxException {
        // 1. Create a Lock
        ReentrantLock lock = new ReentrantLock();

        // 2. Create an instance of this class
        ReentrantLockBasedPlanetAnalyzerApp app =
                new ReentrantLockBasedPlanetAnalyzerApp(lock);

        // 3. Concurrently sample files
        Double average = app.getAverageOfTemperatureFiles(files);

        // 4. Report average
        System.out.println("Calculated a mean of " + average);
    }

    public ReentrantLockBasedPlanetAnalyzerApp(ReentrantLock lock) {
        // Create a Thread Safe Sampler with the Lock
        sampler = new ThreadSafePlanetSampler(lock);
        fileAnalyzer = new ThreadSafePlanetFileAnalyzer(sampler);
    }

    private  Double getAverageOfTemperatureFiles(String[] files) throws InterruptedException, ExecutionException, URISyntaxException {
        // Get file paths
        String fileOne = files[0];
        String fileTwo = files[1];
        Path fileOnePath = getCSVPath(fileOne);
        Path fileTwoPath = getCSVPath(fileTwo);

        Double average;
        ExecutorService executorService = null;
        try {
            // 1. ThreadPool
            executorService = Executors.newFixedThreadPool(2);

            // 2. Submit Each Future for summing
            Future futureOfFileOne = executorService.submit(() -> fileAnalyzer.sampleTemperaturesFromFile(fileOnePath));
            Future futureOfFileTwo = executorService.submit(() -> fileAnalyzer.sampleTemperaturesFromFile(fileTwoPath));

            // 3. Wait for Futures
            futureOfFileOne.get();
            futureOfFileTwo.get();

            // 4. Fetch the average
            average = sampler.getAverage();
        } finally {
            if (null != executorService) {
                executorService.shutdown();
            }
        }
        return average;
    }

    private Path getCSVPath(String file) throws URISyntaxException {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        URL resource = loader.getResource(file);
        if (resource == null) {
            throw new RuntimeException("Bad file name provided");
        }
        return Paths.get(resource.toURI());
    }
}


