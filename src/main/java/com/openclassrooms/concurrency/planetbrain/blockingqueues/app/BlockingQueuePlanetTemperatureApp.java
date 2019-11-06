package com.openclassrooms.concurrency.planetbrain.blockingqueues.app;

import com.openclassrooms.concurrency.planetbrain.blockingqueues.service.BlockingQueueFileAnalyzer;
import com.openclassrooms.concurrency.planetbrain.model.AveragingComponents;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class BlockingQueuePlanetTemperatureApp {
    public static final int FILE_CONSUMER_COUNT = 30;

    public static void main(String[] args) throws URISyntaxException, IOException, InterruptedException, ExecutionException {
        URL folderResource = getResourceFolderUrl(args[0]);
        BlockingQueue<Path> fileQueue = new LinkedBlockingQueue<>();
        BlockingQueue<AveragingComponents> temperatureQueue = new LinkedBlockingQueue<>();

        // Let's break this down to explicit steps!

        // 1. Publish file paths to the file Queue
        int fileCount = (int) publishFilesFromFolder(folderResource, fileQueue);

        // 2. Publish temperatures from all files to our queue
        CountDownLatch fileLatch = new CountDownLatch(fileCount);
        BlockingQueueFileAnalyzer analyzer = new BlockingQueueFileAnalyzer(fileQueue, temperatureQueue, fileLatch);
        CompletableFuture<Void> fileAnalyzingFutures = createFileAnalyzingFutures(analyzer);

        // 3. Consume averages asynchronously until all 23 file's totals are seen
        CountDownLatch averagingLatch = new CountDownLatch(fileCount);
        CompletableFuture<AveragingComponents> componentsFuture = consumeAverages(temperatureQueue, averagingLatch);

        // 4. Wait for averages AND planet threads to finish
        fileLatch.await();
        averagingLatch.await();

        // 5. Fetch the result from our completed component Future
        AveragingComponents averagingComponents = componentsFuture.get();

        // 6. Calculate the average
        Double average = averagingComponents.getSampleSum()/averagingComponents.getSampleSize();
        System.out.println("Computed an average of " + average);

        // 7. Clean up in case we still have blocking consumers
        if (!fileAnalyzingFutures.isDone()) {
            fileAnalyzingFutures.cancel(true);
        }
    }

    private static CompletableFuture<AveragingComponents> consumeAverages(
            BlockingQueue<AveragingComponents> temperatureQueue, CountDownLatch averagingLatch) {
        return CompletableFuture.supplyAsync(()->{
            Double totalTemperature = 0.0;
            Double totalSampleSize = 0.0;
            while (averagingLatch.getCount() != 0) {
                try {
                    AveragingComponents averagingComponents = temperatureQueue.take();
                    totalSampleSize += averagingComponents.getSampleSize();
                    totalTemperature += averagingComponents.getSampleSum();
                } catch (InterruptedException e) {
                } finally {
                    averagingLatch.countDown();
                }
            }
            return new AveragingComponents(totalSampleSize, totalTemperature);
        });
    }


    private static CompletableFuture<Void> createFileAnalyzingFutures(BlockingQueueFileAnalyzer analyzer) {
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        for(int futureNumber = 0; futureNumber< FILE_CONSUMER_COUNT; futureNumber++) {
            CompletableFuture<Void> future = CompletableFuture.runAsync(
                    ()->{
                        try {
                            analyzer.consumeFilesWhenReady();
                        } catch (InterruptedException|IOException e) {
                            e.printStackTrace();
                        }
                    }
            );
            futures.add(future);
        }
        return CompletableFuture.allOf( futures.toArray(new CompletableFuture[futures.size()]) );
    }

    private static long publishFilesFromFolder(URL folderResource, BlockingQueue<Path> fileQueue) throws IOException, URISyntaxException {
        return Files.walk(Paths.get(folderResource.toURI()))
                .filter(Files::isRegularFile)
                .map(filePath -> {
                    try {
                        fileQueue.put(filePath);
                    } catch (InterruptedException e) {
                        // TODO: handle errors in a real app
                    };
                    return filePath;
                }).count();
    }

    private static URL getResourceFolderUrl(String arg) {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        return loader.getResource(arg);
    }
}
