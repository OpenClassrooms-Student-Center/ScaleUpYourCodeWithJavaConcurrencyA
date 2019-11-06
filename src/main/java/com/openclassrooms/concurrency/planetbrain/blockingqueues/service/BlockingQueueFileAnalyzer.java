package com.openclassrooms.concurrency.planetbrain.blockingqueues.service;

import com.openclassrooms.concurrency.planetbrain.model.AveragingComponents;
import com.openclassrooms.concurrency.planetbrain.model.KeplerCsvFields;
import com.openclassrooms.concurrency.planetbrain.threads.service.ThreadBasedPlanetFileAnalyzer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.stream.DoubleStream;

public class BlockingQueueFileAnalyzer extends ThreadBasedPlanetFileAnalyzer {

    private final BlockingQueue<Path> fileQueue;
    private final CountDownLatch fileLatch;
    private final BlockingQueue<AveragingComponents> temperatureQueue;

    public BlockingQueueFileAnalyzer(BlockingQueue<Path> fileQueue, BlockingQueue<AveragingComponents> temperatureQueue, CountDownLatch fileLatch) {
        this.fileQueue = fileQueue;
        this.temperatureQueue = temperatureQueue;
        this.fileLatch = fileLatch;
    }

    public void consumeFilesWhenReady() throws InterruptedException, IOException {
        // Consume files
        while (fileLatch.getCount() != 0) {
            Path file = fileQueue.take();
            try {
                // Publish temperatures
                publishTemperatures(file);
            } finally {
                // Countdown the file latch
                fileLatch.countDown();
            }
        }
    }

    private void publishTemperatures(Path file) throws IOException, InterruptedException {
        AveragingComponents components = averagingComponents(file);
        temperatureQueue.put(components);

    }

    /**
     * Returns a DoubleStream of temperature values
     * @param path to CSV file with Kepler data
     * @return DoubleStream of temperatures in the file
     * @throws IOException
     */
    private DoubleStream getDoublesFromFile(Path path) throws IOException {
        DoubleStream streamOfDoubles = Files.lines(path).
                // Ignore comments in the CSV
                        filter(line -> !line.startsWith(KeplerCsvFields.COMMENT_CHARACTER)).
                // Skip the header which is left after our comments
                        filter(line -> !line.contains("kepoi_name,kepler_name,koi_teq")).
                // Separate the columns using a comma
                        map(line -> line.split(",")).
                        filter(row -> row.length >= 3).
                // Extract the planet's temperature in Kelvin
                        map(row -> row[KeplerCsvFields.EQUILIBRIUM_TEMPERATURE_COLUMN]).
                // Convert the value to a double and sum
                        mapToDouble(temperatureStringValue -> Double.parseDouble(temperatureStringValue));
        return streamOfDoubles;
    }

}

