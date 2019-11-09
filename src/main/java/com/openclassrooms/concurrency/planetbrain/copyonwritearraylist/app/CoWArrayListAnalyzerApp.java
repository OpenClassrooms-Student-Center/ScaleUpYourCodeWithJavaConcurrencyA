package com.openclassrooms.concurrency.planetbrain.copyonwritearraylist.app;

import com.openclassrooms.concurrency.planetbrain.copyonwritearraylist.service.CoWArrayListAnalyzer;
import com.openclassrooms.concurrency.planetbrain.model.AveragingComponents;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class CoWArrayListAnalyzerApp {
    private static CopyOnWriteArrayList<AveragingComponents> temperatureSamples = new CopyOnWriteArrayList<>();

    public static void main(String[] folders) throws Exception {
        Double average = getAveragesFromFolder(folders);
        System.out.println("Calculated an average of " + average);
    }

    public static Double getAveragesFromFolder(String[] folders) throws IOException, ExecutionException, InterruptedException, URISyntaxException {
        // 1. Create Executor Pool
        List<CompletableFuture<Void>> completableFutures = new ArrayList<>();

        // 2. Get files
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        URL folderResource = loader.getResource(folders[0]);
        List<Path> files = Files.walk(Paths.get(folderResource.toURI()))
                .filter(Files::isRegularFile).collect(Collectors.toList());

        // 3. Create Countdown latch
        CountDownLatch latch = new CountDownLatch(files.size());

        // 4. Create File Analyzer
        CoWArrayListAnalyzer fileAnalyzer = new CoWArrayListAnalyzer(temperatureSamples);

        // 5 loop files and link up CompletableFutures
        for (Path path : files) {
            completableFutures.add(CompletableFuture.runAsync(
                    ()->fileAnalyzer.sampleTemperatures(path)));
        }

        // 6. Run all futures
        CompletableFuture<Void> samplingFutures = CompletableFuture.allOf(
                completableFutures.toArray(new CompletableFuture[]{}));


        // 7. Wait for futures to complete
        samplingFutures.get();

        // 8. Calculate the average and from our CompletableFutures
        return calculateAverage();
    }

    private static Double calculateAverage() {
        Double totalTemperature = 0.0;
        Double sampleCount = 0.0;

        for (AveragingComponents sample : temperatureSamples) {
            totalTemperature += sample.getSampleSum();
            sampleCount += sample.getSampleSize();
        }
        return totalTemperature/sampleCount;
    }
}



