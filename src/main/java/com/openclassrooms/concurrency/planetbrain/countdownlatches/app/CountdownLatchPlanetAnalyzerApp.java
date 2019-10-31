package com.openclassrooms.concurrency.planetbrain.countdownlatches.app;

import com.openclassrooms.concurrency.planetbrain.threads.service.ThreadBasedPlanetFileAnalyzer;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class CountdownLatchPlanetAnalyzerApp {
    private static final Integer NUMBER_OF_FILES = 23;
    private static ThreadBasedPlanetFileAnalyzer fileAnalyzer = new ThreadBasedPlanetFileAnalyzer();

    // Create Latches
    static CountDownLatch summingLatch = new CountDownLatch( NUMBER_OF_FILES );
    static CountDownLatch countingLatch = new CountDownLatch( NUMBER_OF_FILES );

    public static void main(String[] folders) throws IOException, ExecutionException, InterruptedException, URISyntaxException {
        Double average = getAveragesFromFolder(folders);

        System.out.println("Calculated an average of " + average);
    }

    public static Double getAveragesFromFolder(String[] folders) throws IOException, ExecutionException, InterruptedException, URISyntaxException {

        //1. Create Executor Pool
        ExecutorService executorService = Executors.newFixedThreadPool(4);

        try {
            // 2. Create a container for our futures
            List<Future<Double>> futuresListOfFileSums = new ArrayList<>();
            List<Future<Double>> futuresListOfSampleCounts = new ArrayList<>();

            //3. Iterate each folder and farm out work for each file
            for (String folder : folders) {
                ClassLoader loader = Thread.currentThread().getContextClassLoader();
                URL folderResource = loader.getResource(folders[0]);

                Files.walk(Paths.get(folderResource.toURI()))
                        .filter(Files::isRegularFile)
                        .forEach(filePath -> {

                            // 4. Submit work to the pool
                            Future<Double> summingTask = executorService.submit(() -> sumFileAndReleaseLatch(filePath));
                            Future<Double> countingTask = executorService.submit(() -> countFileAndReleaseLatch(filePath));

                            // 5. Store the futures
                            futuresListOfFileSums.add(summingTask);
                            futuresListOfSampleCounts.add(countingTask);
                        });
            }
            // 6. Blocking wait for the countdown latch to reach 0
            System.out.println("Waiting for " + summingLatch.getCount() + " summing latches");
            summingLatch.await();
            System.out.println("The summing latch count has reached 0");

            // 7 Find the total sum
            Double sumOfTemperatures = getSumOfFuturesInList(futuresListOfFileSums);

            // 8. Blocking wait for the countdown latch to reach 0
            System.out.println("Waiting for " + countingLatch.getCount() + " counting latches");
            countingLatch.await();
            System.out.println("The counting latch count has reached 0");

            // 9. Find the total sample size
            Double totalSampleSize = getSumOfFuturesInList(futuresListOfSampleCounts);

            // 10. Calculate the average!
            return sumOfTemperatures / totalSampleSize;
        } finally {
            executorService.shutdown();
        }
    }

    private static Double getSumOfFuturesInList(List<Future<Double>> futuresListOfSampleCounts) throws InterruptedException, ExecutionException {
        Double totalSampleSize = 0.0;
        for (Future<Double> future : futuresListOfSampleCounts) {
            totalSampleSize += future.get();
        }
        return totalSampleSize;
    }

    private static Double sumFileAndReleaseLatch(Path filePath){
        try {
            return fileAnalyzer.sumFile(filePath);
        } catch (IOException e) {
            e.printStackTrace();
            return 0.0;
        } finally {
            // Indicate the file has been processed
            summingLatch.countDown();
        }
    }

    private static Double countFileAndReleaseLatch(Path filePath){
        try {
            return fileAnalyzer.countDoubleRows(filePath);
        } catch (IOException e) {
            e.printStackTrace();
            return 0.0;
        } finally {
            // Indicate the file has been processed
            countingLatch.countDown();
        }
    }
}
