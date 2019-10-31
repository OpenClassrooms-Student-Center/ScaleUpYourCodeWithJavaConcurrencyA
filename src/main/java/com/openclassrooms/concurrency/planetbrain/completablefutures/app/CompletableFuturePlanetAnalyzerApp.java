package com.openclassrooms.concurrency.planetbrain.completablefutures.app;

import com.openclassrooms.concurrency.planetbrain.threads.service.ThreadBasedPlanetFileAnalyzer;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

public class CompletableFuturePlanetAnalyzerApp {
    private static ThreadBasedPlanetFileAnalyzer fileAnalyzer = new ThreadBasedPlanetFileAnalyzer();


    public static void main(String[] folders) throws IOException, ExecutionException, InterruptedException, URISyntaxException {
        Double average = getAveragesFromFolder(folders);

        System.out.println("Calculated an average of " + average);
    }

    public static Double getAveragesFromFolder(String[] folders) throws IOException, ExecutionException, InterruptedException, URISyntaxException {

        // 1. Create initial CompletableFutures for each task
        CompletableFuture<Double> summingCompletableFuture = CompletableFuture.supplyAsync(() -> 0.0);
        CompletableFuture<Double> countingCompletableFuture = CompletableFuture.supplyAsync(() -> 0.0);

        // 2. Get files
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        URL folderResource = loader.getResource(folders[0]);
        List<Path> files = Files.walk(Paths.get(folderResource.toURI()))
                .filter(Files::isRegularFile).collect(Collectors.toList());

        // 3. loop files and link up CompletableFutures
        for (Path filePath : files) {
            summingCompletableFuture =
                    summingCompletableFuture.thenCombineAsync(
                            CompletableFuture.supplyAsync(() -> sumFile(filePath)),
                            (left, right) -> left + right
                    );
            countingCompletableFuture =
                    countingCompletableFuture.thenCombineAsync(
                            CompletableFuture.supplyAsync(() -> countFile(filePath)),
                            (left, right) -> left + right
                    );
        }

        // 4. Calculate the average and from our CompletableFutures
        return summingCompletableFuture.get() / countingCompletableFuture.get();
    }

    private static Double getSumOfFuturesInList(List<Future<Double>> futuresListOfSampleCounts) throws InterruptedException, ExecutionException {
        Double totalSampleSize = 0.0;
        for (Future<Double> future : futuresListOfSampleCounts) {
            totalSampleSize += future.get();
        }
        return totalSampleSize;
    }

    private static Double sumFile(Path filePath) {
        try {
            return fileAnalyzer.sumFile(filePath);
        } catch (IOException e) {
            e.printStackTrace();
            return 0.0;
        }
    }

    private static Double countFile(Path filePath) {
        try {
            return fileAnalyzer.countDoubleRows(filePath);
        } catch (IOException e) {
            e.printStackTrace();
            return 0.0;
        }
    }
}
