package com.openclassrooms.concurrency.planetbrain.threads.service;

import com.openclassrooms.concurrency.planetbrain.model.AveragingComponents;
import com.openclassrooms.concurrency.planetbrain.model.KeplerCsvFields;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.stream.DoubleStream;

public class ThreadBasedPlanetFileAnalyzer {


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

    /**
     * Returns the sum of all temperatures in a file
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
     * @param path to CSV file with Kepler data
     * @return Count of all temperatures in a file
     * @throws IOException
     */
    public Double countDoubleRows(Path path) throws IOException {
        DoubleStream temperaturesInFile = getDoublesFromFile(path);
        return (double) temperaturesInFile.count();
    }

    /**
     * Returns an AveragingComponent with the sampleSize and Sum
     * @param path
     * @return AveragingComponent with the parts required to calculate the mean
     * @throws IOException
     */
    public AveragingComponents averagingComponents(Path path) throws IOException {
        DoubleStream temperaturesInFile = getDoublesFromFile(path);

        //Double sampleSize = Double.valueOf(temperaturesInFile.count());
        Optional<AveragingComponents> temperatureSum = temperaturesInFile.
                mapToObj((temp) -> new AveragingComponents(1.0, temp)).
                reduce((a, b)->
                        new AveragingComponents(
                                a.getSampleSize()+b.getSampleSize(),
                                a.getSampleSum()+b.getSampleSum()));

        return temperatureSum.get();
    }
}
