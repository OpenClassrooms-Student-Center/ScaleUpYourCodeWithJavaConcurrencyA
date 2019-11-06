package com.openclassrooms.concurrency.planetbrain.concurrenthashmap.app;

import com.openclassrooms.concurrency.planetbrain.concurrenthashmap.service.ThreadSafeMapFilteringFileAnalyzer;
import com.openclassrooms.concurrency.planetbrain.concurrenthashmap.service.ThreadSafeMapFilteringFileAnalyzer.DedupingScheme;

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

public class ThreadSafeMapAnalyzerApp {


    public static void main(String[] args) throws URISyntaxException, IOException, InterruptedException, ExecutionException {
        String folder = args[0];
        DedupingScheme scheme = DedupingScheme.valueOf(args[1]);
        Double average = getAveragesFromFolder(folder, scheme);

        System.out.println("Calculated an average of " + average);
    }

    public static Double getAveragesFromFolder(String folder, DedupingScheme scheme) throws IOException, ExecutionException, InterruptedException, URISyntaxException {
        ThreadSafeMapFilteringFileAnalyzer fileAnalyzer = new ThreadSafeMapFilteringFileAnalyzer(scheme);

        // 1. Create initial CompletableFutures for each task
        CompletableFuture<Double> summingCompletableFuture = CompletableFuture.supplyAsync(() -> 0.0);
        CompletableFuture<Double> countingCompletableFuture = CompletableFuture.supplyAsync(() -> 0.0);

        // 2. Get files
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        URL folderResource = loader.getResource(folder);
        List<Path> files = Files.walk(Paths.get(folderResource.toURI()))
                .filter(Files::isRegularFile).collect(Collectors.toList());

        // 3. loop files and link up CompletableFutures
        for (Path filePath : files) {
            summingCompletableFuture =
                    summingCompletableFuture.thenCombineAsync(
                            CompletableFuture.supplyAsync(() -> sumFile(filePath, fileAnalyzer)),
                            (left, right) -> left + right
                    );
            countingCompletableFuture =
                    countingCompletableFuture.thenCombineAsync(
                            CompletableFuture.supplyAsync(() -> countFile(filePath, fileAnalyzer)),
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

    private static Double sumFile(Path filePath, ThreadSafeMapFilteringFileAnalyzer fileAnalyzer) {
        try {
            return fileAnalyzer.sumFile(filePath);
        } catch (IOException e) {
            e.printStackTrace();
            return 0.0;
        }
    }

    private static Double countFile(Path filePath, ThreadSafeMapFilteringFileAnalyzer fileAnalyzer) {
        try {
            return fileAnalyzer.countDoubleRows(filePath);
        } catch (IOException e) {
            e.printStackTrace();
            return 0.0;
        }
    }
}
