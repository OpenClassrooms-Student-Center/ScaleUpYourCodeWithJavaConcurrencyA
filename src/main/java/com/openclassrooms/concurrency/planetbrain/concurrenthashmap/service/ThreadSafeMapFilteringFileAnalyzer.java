package com.openclassrooms.concurrency.planetbrain.concurrenthashmap.service;

import com.openclassrooms.concurrency.planetbrain.model.KeplerCsvFields;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.DoubleStream;

public class ThreadSafeMapFilteringFileAnalyzer {


    public enum DedupingScheme {
        CONCURRENT_HASHMAP,
        SYNCHRONIZED_HASHMAP,
    }

    private Map<String, Double> planetsSeen;

    public ThreadSafeMapFilteringFileAnalyzer(DedupingScheme dedupingScheme) {
        if (DedupingScheme.CONCURRENT_HASHMAP.equals(dedupingScheme)) {
            planetsSeen = new ConcurrentHashMap<>();
        } else if (DedupingScheme.SYNCHRONIZED_HASHMAP.equals(dedupingScheme)) {
            planetsSeen = Collections.synchronizedMap(new HashMap<>());
        }
    }

    /**
     * Returns a DoubleStream of temperature values
     *
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
                        filter(this::planetHasNotBeenSampled).
                // Extract the planet's temperature in Kelvin
                        map(row -> row[KeplerCsvFields.EQUILIBRIUM_TEMPERATURE_COLUMN]).
                // Convert the value to a double and sum
                        mapToDouble(temperatureStringValue -> Double.parseDouble(temperatureStringValue));
        return streamOfDoubles;
    }

    /**
     * Returns the sum of all temperatures in a file
     *
     * @param path to CSV file with Kepler data
     * @return The sum of all temperatures
     * @throws IOException
     */
    public Double sumFile(Path path) throws IOException {
        DoubleStream temperaturesInFile = getDoublesFromFile(path);
        return temperaturesInFile.sum();
    }

    /**
     * Returns a count of all temperatures in a file
     *
     * @param path to CSV file with Kepler data
     * @return Count of all temperatures in a file
     * @throws IOException
     */
    public Double countDoubleRows(Path path) throws IOException {
        DoubleStream temperaturesInFile = getDoublesFromFile(path);
        return (double) temperaturesInFile.count();
    }

    /**
     * Checks if this planet has been seen before using a thread safe data structure
     *
     * @param row array with CSV fields
     * @return true if this planet has NOT been seen before.
     */
    private boolean planetHasNotBeenSampled(String[] row) {
        // planet details for this row
        String planet = row[ KeplerCsvFields.KEPOI_NAME_COLUMN ];
        Double temperature = Double.parseDouble(row[ KeplerCsvFields.EQUILIBRIUM_TEMPERATURE_COLUMN ]);

        Double lastValue = planetsSeen.get(planet);

        // return early and skip a value if it hasn't changed
        if (null != lastValue && lastValue == temperature) {
            return false;
        }

        // count this planet and temperature + update our map
        planetsSeen.put(planet, temperature);
        
        return true;
    }


}
