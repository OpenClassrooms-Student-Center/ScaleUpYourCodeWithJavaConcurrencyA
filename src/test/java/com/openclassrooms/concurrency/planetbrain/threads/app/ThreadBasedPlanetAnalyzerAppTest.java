package com.openclassrooms.concurrency.planetbrain.threads.app;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.net.URISyntaxException;
import java.util.concurrent.ExecutionException;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Given a raw thread implementation of the planet analyser")
class ThreadBasedPlanetAnalyzerAppTest {
    private static final String[] SPLIT_PLANETS_FILES = {"first-55820.csv", "last-55820.csv"};

    @Test
    @DisplayName("When presented with a file of plananetry data, Then it should return the average")
    public void itShouldCalculateAnAverageFromAKeplerFiles() throws URISyntaxException, InterruptedException, ExecutionException, URISyntaxException {
        Double result = ThreadBasedPlanetAnalyzerApp.getAverageOfTemperatureFiles(SPLIT_PLANETS_FILES);

        Double expectedResult = 1149.1999820257033;
        assertThat(result, is(equalTo(expectedResult)));
    }
}