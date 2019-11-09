package com.openclassrooms.concurrency.planetbrain.copyonwritearraylist.service;

import com.openclassrooms.concurrency.planetbrain.model.AveragingComponents;
import com.openclassrooms.concurrency.planetbrain.model.KeplerCsvFields;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.DoubleStream;

public class CoWArrayListAnalyzer {
    private final CopyOnWriteArrayList<AveragingComponents> samples;

    public CoWArrayListAnalyzer(CopyOnWriteArrayList<AveragingComponents> container) {
        this.samples = container;
    }

    /**
     * Samples the temperatures in a file
     * @param file path to kepler data
     * @return AveragingComponents describing the samples of temperatures in this file
     */
    public void sampleTemperatures(Path file) {
        try {
            // Using a mutable reduction count and sum all the temperatures in this file
            Optional<AveragingComponents> components = getDoublesFromFile(file)
                    .mapToObj((n)->new AveragingComponents(1.0, n))
                    .reduce( (m, n) -> new AveragingComponents(
                            m.getSampleSize() + n.getSampleSize(),
                            m.getSampleSum() + n.getSampleSum()) );

            // Add the result to our SHARED samples array
            samples.add(
                    components.orElse(new AveragingComponents(0.0, 0.0)));
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
