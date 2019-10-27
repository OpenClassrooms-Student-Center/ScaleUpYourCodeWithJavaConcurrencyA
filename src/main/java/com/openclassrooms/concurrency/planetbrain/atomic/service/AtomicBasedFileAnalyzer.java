package com.openclassrooms.concurrency.planetbrain.atomic.service;

import com.openclassrooms.concurrency.planetbrain.model.KeplerCsvFields;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.DoubleStream;

public class AtomicBasedFileAnalyzer {

    // Our atomics
    private AtomicInteger sampleSize = new AtomicInteger(0);
    private AtomicReference<Double> temperatureSum = new AtomicReference<>(0.0);

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
                        skip(1).
                // Separate the columns using a comma
                        map(line -> line.split(",")).
                        filter(row -> row.length >= 3).
                // Extract the planet's temperature in Kelvin
                        map(row -> row[KeplerCsvFields.EQUILIBRIUM_TEMPERATURE_COLUMN]).
                // Convert the value to a double and sum
                        mapToDouble(temperatureStringValue -> Double.parseDouble(temperatureStringValue));
        return streamOfDoubles;
    }

    /**
     * Processes each temperature in the file and updates
     * a atomic counters
     * @param path to file
     */
    public void processFile(Path path) {
        try {
            getDoublesFromFile(path)
                    .forEach(value -> {
                        // atomically increment the sample size
                        sampleSize.incrementAndGet();

                        // atomically update the counter
                        temperatureSum.updateAndGet((current -> {
                            return current + value;
                        }));
                    });
        } catch (IOException e) {
            throw new RuntimeException("Error reading file", e);
        }
    }

    /**
     * Returns the current value of the AtomicInteger
     * @return sample size
     */
    public Integer getSampleSize() {
        return sampleSize.get();
    }

    /**
     * Returns the current value of the DoubleReference
     * @return sum of temperatures
     */
    public Double getTemperatureSum() {
        return temperatureSum.get();
    }
}
