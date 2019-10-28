package com.openclassrooms.concurrency.planetbrain.semaphores.app;

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

public class SemaphorePlanetAnalyzerApp {
    private static ThreadBasedPlanetFileAnalyzer fileAnalyzer = new ThreadBasedPlanetFileAnalyzer();

    // Create Semaphore of Size 8
    static Semaphore semaphore = new Semaphore(8);

    public static void main(String[] folders) throws IOException, ExecutionException, InterruptedException, URISyntaxException {
        Double average = getAveragesFromFolder(folders);

        System.out.println("Calculated an average of "+ average);
    }

    public static Double getAveragesFromFolder(String[] folders) throws IOException, ExecutionException, InterruptedException, URISyntaxException {

        //1. Create Executor Pool
        ExecutorService executorService = Executors.newFixedThreadPool(20);

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
                            Future<Double> summingTask = executorService.submit(() -> sumFileWithSemaphores(filePath));
                            Future<Double> countingTask = executorService.submit(() -> countFileWithSemaphores(filePath));

                            // 5. Store the futures
                            futuresListOfFileSums.add(summingTask);
                            futuresListOfSampleCounts.add(countingTask);
                        });
            }

            // 6 Find the total sum
            Double sumOfTemperatures = 0.0;
            for (Future<Double> future : futuresListOfFileSums) {
                sumOfTemperatures += future.get();
            }

            // 7 Find the total sample size
            Double totalSampleSize = 0.0;
            for (Future<Double> future : futuresListOfSampleCounts) {
                totalSampleSize += future.get();
            }

            // Calculate the average!
            return sumOfTemperatures / totalSampleSize;
        } finally {
            executorService.shutdown();
        }
    }

    private static Double sumFileWithSemaphores(Path path) throws InterruptedException {
        Double result = null;
        try {
            // ACQUIRE A SEMAPHORE
            semaphore.acquire();
            result = fileAnalyzer.sumFile(path);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // RELEASE A SEMAPHORE
            semaphore.release();
        }
        return result;
    }

    private static Double countFileWithSemaphores(Path path) throws InterruptedException {
        Double result = null;
        try {
            // ACQUIRE A SEMAPHORE
            semaphore.acquire();
            result = fileAnalyzer.countDoubleRows(path);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // RELEASE A SEMAPHORE
            semaphore.release();
        }
        return result;
    }

}
