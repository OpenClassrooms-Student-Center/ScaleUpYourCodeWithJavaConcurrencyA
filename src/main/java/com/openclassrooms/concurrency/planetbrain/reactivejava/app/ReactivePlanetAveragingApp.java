package com.openclassrooms.concurrency.planetbrain.reactivejava.app;

import com.openclassrooms.concurrency.planetbrain.model.KeplerCsvFields;
import com.openclassrooms.concurrency.planetbrain.reactivejava.subscriber.TemperatureSubscriber;
import io.reactivex.Flowable;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.DoubleStream;

public class ReactivePlanetAveragingApp {

    public static void main(String[] args) throws URISyntaxException, IOException {
        TemperatureSubscriber temperatureSubscriber = new TemperatureSubscriber();
        Path filePath = getCSVPath(args[0]);

        // Fetch a file from a stream
        DoubleStream temperatureStream = getDoublesFromFile(filePath);

        // Your challenge is to subscribe to this stream the TemperatureSubscriber class
        Flowable<Double> flowableTemperatures =
                Flowable.fromIterable(temperatureStream::iterator).
                        onBackpressureDrop();
    }


    /**
     * Returns a DoubleStream of temperature values
     * @param path to CSV file with Kepler data
     * @return DoubleStream of temperatures in the file
     * @throws IOException
     */
    private static DoubleStream getDoublesFromFile(Path path) throws IOException {
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

    private static Path getCSVPath(String file) throws URISyntaxException {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        URL resource = loader.getResource(file);
        if (resource == null) {
            throw new RuntimeException("Bad file name provided");
        }
        return Paths.get(resource.toURI());
    }
}
