package com.openclassrooms.concurrency.planetbrain.app;

import com.openclassrooms.concurrency.planetbrain.model.PlanetStats;
import com.openclassrooms.concurrency.planetbrain.service.PlanetFileAnalyser;

import java.math.BigDecimal;
import java.net.URI;
import java.net.URL;
import java.time.Duration;
import java.time.Instant;
import java.util.logging.Logger;

public class PlanetTemperatureAnalyzer {
    private final static Logger LOGGER = Logger.getLogger(PlanetTemperatureAnalyzer.class.getName());
    private final static Double HABITABLE_EARTH_TEMPERATURE_KELVIN = 288.0;

    private PlanetFileAnalyser planetFileAnalyser;

    public PlanetTemperatureAnalyzer(PlanetFileAnalyser planetFileAnalyser) {
        this.planetFileAnalyser = planetFileAnalyser;
    }

    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            throw new RuntimeException("Please provide the name of at least one kepler file");
        }

        // Measure start time
        Instant startTime = Instant.now();

        // We do this to ensure constructor injection
        PlanetFileAnalyser planetFileAnalyser = new PlanetFileAnalyser();
        PlanetTemperatureAnalyzer analyzerApp = new PlanetTemperatureAnalyzer(planetFileAnalyser);

        String file = args[0];
        // Read the current file and calculate the average temperature warmer than Earth
        // and the average temperature cooler than Earth.
        PlanetStats results = analyzerApp.analyzeFile(file);
        Instant endTime = Instant.now();
        Duration duration = Duration.between(startTime, endTime);

        LOGGER.info("File["+file+"] Cool planet temperature: " + results.getCoolAverageInKelvin() + " Kelvin");
        LOGGER.info("File["+file+"] Hot planet temperature: " + results.getHotAverageInKelvin() + " Kelvin");
        LOGGER.info("File["+file+"] Processing took: " + duration.getNano() + " nano seconds");
    }

    public PlanetStats analyzeFile(String file) throws Exception {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        URL resource = loader.getResource(file);
        if (resource == null) {
            throw new RuntimeException("Bad file name provided");
        }
        URI fileURI = resource.toURI();

        BigDecimal coolMeanTemperature = planetFileAnalyser.calculateAveragesFor(
                    fileURI, HABITABLE_EARTH_TEMPERATURE_KELVIN);

        BigDecimal hotMeanTemperature = planetFileAnalyser.calculateAveragesFor(
                    fileURI, Double.MAX_VALUE);

        return new PlanetStats(coolMeanTemperature, hotMeanTemperature);
    }


}
