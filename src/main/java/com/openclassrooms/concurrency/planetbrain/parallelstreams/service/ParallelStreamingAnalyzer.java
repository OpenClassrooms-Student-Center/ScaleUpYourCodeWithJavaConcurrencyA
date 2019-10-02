package com.openclassrooms.concurrency.planetbrain.parallelstreams.service;

import com.openclassrooms.concurrency.planetbrain.model.KeplerCsvFields;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;

public class ParallelStreamingAnalyzer {

    /**
     * Calculates the mean temperature from the Kepler file in the resources folder
     * @param fileURI of the kepler file
     * @return A double containing the mean temperature in the file
     * @throws IOException
     */
    public Double calculateAverageFor(URI fileURI) throws IOException {
        Path path = Paths.get(fileURI);
        Double meanTemperature = Files.lines(path).
                // Parallelize!
                        parallel().
                // Ignore comments in the CSV
                        filter(line -> !line.startsWith(KeplerCsvFields.COMMENT_CHARACTER)).
                // Skip the header which is left after our comments
                        skip(1).
                // Separate the columns using a comma
                        map(line -> line.split(",")).
                        filter(row -> row.length >= 3).
                // Extract the planet's temperature in Kelvin
                        map(row -> row[KeplerCsvFields.EQUILIBRIUM_TEMPERATURE_COLUMN]).
                // Convert the value to a double
                        map(temperatureStringValue -> Double.parseDouble(temperatureStringValue)).
                // Update the average with a mutable reduction (calculate a running average)
                        collect(
                        Collectors.averagingDouble(num -> num));

        return meanTemperature;
    }

}
