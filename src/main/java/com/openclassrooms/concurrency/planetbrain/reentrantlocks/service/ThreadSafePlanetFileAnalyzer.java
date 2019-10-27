package com.openclassrooms.concurrency.planetbrain.reentrantlocks.service;

import com.openclassrooms.concurrency.planetbrain.model.KeplerCsvFields;
import com.openclassrooms.concurrency.planetbrain.reentrantlocks.model.ThreadSafePlanetSampler;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.DoubleStream;

public class ThreadSafePlanetFileAnalyzer {

    private ThreadSafePlanetSampler threadSafePlanetSampler;

    /**
     * Creates an instance of the ThreadSafePlanetFileAnalyzer which
     * stores its temperatures in the provided ThreadSafePlanetSampler
     * @param threadSafePlanetSampler
     */
    public ThreadSafePlanetFileAnalyzer(ThreadSafePlanetSampler threadSafePlanetSampler){
        this.threadSafePlanetSampler = threadSafePlanetSampler;
    }

    /**
     * Samples the temperatures from the provided file.
     * @param path to a Kepler file
     * @throws IOException
     */
    public void sampleTemperaturesFromFile(Path path) {
        try {
            getDoublesFromFile(path).forEach(
                    (sample) -> threadSafePlanetSampler.addSample(sample)
            );
        } catch (IOException e) {
            e.printStackTrace();
        }
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
}
