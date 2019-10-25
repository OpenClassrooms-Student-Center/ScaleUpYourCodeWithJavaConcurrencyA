package com.openclassrooms.concurrency.planetbrain.threads.app;

import com.openclassrooms.concurrency.planetbrain.threads.service.ThreadBasedPlanetFileAnalyzer;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

public class ThreadBasedPlanetAnalyzerApp {

    // Create an instance of the analyzer service
    static ThreadBasedPlanetFileAnalyzer fileAnalyzer = new ThreadBasedPlanetFileAnalyzer();

    public static void main(String[] files) throws ExecutionException, InterruptedException, URISyntaxException {
        Double meanTemperature = getAverageOfTemperatureFiles(files);
        System.out.println("Calculated a mean of " + meanTemperature);
    }

    public static Double getAverageOfTemperatureFiles(String[] files) throws InterruptedException, ExecutionException, URISyntaxException {
        // Get file paths
        String fileOne = files[0];
        String fileTwo = files[1];
        Path fileOnePath = getCSVPath(fileOne);
        Path fileTwoPath = getCSVPath(fileTwo);

        // 1. Create Callables for summing
        Callable<Double> sumOfFileOne = ()-> fileAnalyzer.sumFile(fileOnePath);
        Callable<Double> sumOfFileTwo = ()-> fileAnalyzer.sumFile(fileTwoPath);

        // 2. Create Callables for counting
        Callable<Double> countOfFileOne = ()-> fileAnalyzer.countDoubleRows(fileOnePath);
        Callable<Double> countOfFileTwo = ()-> fileAnalyzer.countDoubleRows(fileTwoPath);

        // 3. Create FutureTasks for summing
        FutureTask<Double> sumFileOneFuture = new FutureTask<>(sumOfFileOne);
        FutureTask<Double> sumFileTwoFuture = new FutureTask<>(sumOfFileTwo);

        // 4. Create FutureTasks for counting
        FutureTask<Double> countFileOneFuture = new FutureTask<>(countOfFileOne);
        FutureTask<Double> countFileTwoFuture = new FutureTask<>(countOfFileTwo);

        // 5. Create Threads
        Thread t1Sum = new Thread(sumFileOneFuture);
        Thread t2Sum = new Thread(sumFileTwoFuture);
        Thread t1Count = new Thread(countFileOneFuture);
        Thread t2Count = new Thread(countFileTwoFuture);

        // 6. Run threads
        t1Sum.start();
        t2Sum.start();
        t1Count.start();
        t2Count.start();

        // 7. Wait for FutureTask
        Double valueOfFileOneSum = sumFileOneFuture.get();
        Double valueOfFileTwoSum = sumFileTwoFuture.get();
        Double valueOfFileOneCount = countFileOneFuture.get();
        Double valueOfFileTwoCount = countFileTwoFuture.get();

        // 8. Add sums
        Double sumOfTemperatures = valueOfFileOneSum + valueOfFileTwoSum;

        // 9. Add counts
        Double countOfTemperatures = valueOfFileOneCount + valueOfFileTwoCount;

        // 10. Calculate average
        return sumOfTemperatures/countOfTemperatures;
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
