package com.openclassrooms.concurrency.planetbrain.forkjoin.app;

import com.openclassrooms.concurrency.planetbrain.forkjoin.app.task.TemperatureSummingTask;
import com.openclassrooms.concurrency.planetbrain.model.AveragingComponents;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Collectors;

public class ForkJoinPlanetAnalyzerApp {

    public static void main(String[] folders) throws URISyntaxException, IOException {

        // 1. Get files
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        URL folderResource = loader.getResource(folders[0]);
        List<Path> files = Files.walk(Paths.get(folderResource.toURI()))
                .filter(Files::isRegularFile).collect(Collectors.toList());

        // 2. Create ForkJoin pool
        ForkJoinPool forkJoinPool = new ForkJoinPool();

        // 3. Create our first task
        TemperatureSummingTask temperatureSummingTask = new TemperatureSummingTask(files);

        // 4. Invoke the job in the pool
        AveragingComponents results = forkJoinPool.invoke(temperatureSummingTask);

        // 5. Calculate average
        Double average = results.getSampleSum() / results.getSampleSize();

        System.out.println("Average from forkjoin " + average);
    }


}
