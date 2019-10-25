package com.openclassrooms.concurrency.planetbrain.futures.app;

import com.openclassrooms.concurrency.planetbrain.threads.service.ThreadBasedPlanetFileAnalyzer;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class FutureBasedPlanetAnalyzerApp {

    // Create an instance of the analyzer service
    private static ThreadBasedPlanetFileAnalyzer fileAnalyzer = new ThreadBasedPlanetFileAnalyzer();

    public static void main(String[] files) throws InterruptedException, ExecutionException, URISyntaxException {
        Double meanTemperature = getAverageOfTemperatureFiles(files);
        System.out.println("Calculated a mean of " + meanTemperature);
    }

    private static Double getAverageOfTemperatureFiles(String[] files) throws ExecutionException, InterruptedException, URISyntaxException {
        // Get file paths
        String fileOne = files[0];
        String fileTwo = files[1];
        Path fileOnePath = getCSVPath(fileOne);
        Path fileTwoPath = getCSVPath(fileTwo);

        // 1. ThreadPool
        ExecutorService executorService = Executors.newFixedThreadPool(4);

        // 2. Submit Each Future for summing
        Future<Double> futureOfSumOne = executorService.submit(()-> fileAnalyzer.sumFile(fileOnePath));
        Future<Double> futureOfSumTwo = executorService.submit(()-> fileAnalyzer.sumFile(fileTwoPath));

        // 3. Submit Each Future for counting
        Future<Double> futureOfCountOne = executorService.submit(()-> fileAnalyzer.countDoubleRows(fileOnePath));
        Future<Double> futureOfCountTwo = executorService.submit(()-> fileAnalyzer.countDoubleRows(fileTwoPath));

        // 4. Wait for Futures
        Double valueOfFileOneSum = futureOfSumOne.get();
        Double valueOfFileTwoSum = futureOfSumTwo.get();

        Double valueOfFileOneCount = futureOfCountOne.get();
        Double valueOfFileTwoCount = futureOfCountTwo.get();

        // Your solution, should cover the following behaviour:
        // Ideally you will refactor this to have smaller methods and less logic
        // in the main class.
        Future<Double> futureAverage = executorService.submit(
                () -> {
                    // 5. Add sums
                    Double sumOfTemperatures = valueOfFileOneSum + valueOfFileTwoSum;

                    // 6. Add counts
                    Double countOfTemperatures = valueOfFileOneCount + valueOfFileTwoCount;

                    // 7. Calculate average
                    return sumOfTemperatures / countOfTemperatures;
                });

        // Shutdown Executor Service since we're done with it
        executorService.shutdown();

        // Is there value in having used a future here.
        // Remember, just because we can, it does not meant hat we should!
        return futureAverage.get();
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
