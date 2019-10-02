package com.openclassrooms.concurrency.planetbrain.parallelstreams.app;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.net.URISyntaxException;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@DisplayName("Given a Parallel Streams based calculator")
class PlanetAnalyzerUsingParallelStreamsIntegrationTest {
    public static final String PLANETS_AND_TEMPERATURES_CSV = "planets-and-temperatures.csv";

    @Test
    @DisplayName("When presented with a file of plananetry data, Then it should return the average")
    public void itShouldCalculateAnAverageFromAKeplerFile() throws URISyntaxException {
        Double result = PlanetAnalyzerUsingParallelStreams.analyzeFile(PLANETS_AND_TEMPERATURES_CSV);
        Double expectedResult = 1085.3858276274318;
        assertThat(result, is(equalTo(expectedResult)));
    }
}