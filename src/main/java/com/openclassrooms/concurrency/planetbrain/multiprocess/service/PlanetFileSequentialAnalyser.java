package com.openclassrooms.concurrency.planetbrain.multiprocess.service;

import com.openclassrooms.concurrency.planetbrain.model.KeplerCsvFields;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;

public class PlanetFileSequentialAnalyser {

    public BigDecimal calculateAveragesFor(URI fileURI, Double maxTemperature) throws IOException {
        Path path = Paths.get(fileURI);
        Double meanTemperature = Files.lines(path).
                // Ignore comments in the CSV
                filter(line -> ! line.startsWith(KeplerCsvFields.COMMENT_CHARACTER)).
                // Skip the header which is left after our comments
                skip(1).
                // Separate the columns using a comma
                map(line -> line.split(",")).
                filter(row -> row.length >= 3).
                // Extract the planet's temperature in Kelvin
                map(row -> row[KeplerCsvFields.EQUILIBRIUM_TEMPERATURE_COLUMN]).
                // Convert the value to a double
                map(temperatureStringValue -> Double.parseDouble(temperatureStringValue)).
                // Filter against an upper bound
                filter(kelvinTemperature -> kelvinTemperature <= maxTemperature).
                // Update the average with a mutable reduction (calculate a running average)
                collect(
                        Collectors.averagingDouble(num->num));

        // Return
        return BigDecimal.valueOf(meanTemperature);
    }


}
