package com.openclassrooms.concurrency.planetbrain.multiprocess.app;

import com.openclassrooms.concurrency.planetbrain.model.SplitDataPlanetStats;
import com.openclassrooms.concurrency.planetbrain.multiprocess.service.PlanetFileSequentialAnalyser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.net.URI;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.core.AllOf.allOf;

@DisplayName("Given that the PlanetBrain app is running")
public class PlanetTemperatureAnalyzerIntegrationTest {
    PlanetFileSequentialAnalyser fileAnalyser = new PlanetFileSequentialAnalyser();
    // This is the class which does all the work
    PlanetTemperatureAnalyzer classUnderTest = new PlanetTemperatureAnalyzer(fileAnalyser);

    @Test
    @DisplayName("as a single process, when provided with a single file then it should calculate two averages")
    public void analyzeFile_shouldCalculateBothAveragesTemperatures_whenPresentedWithASingleFile() throws Exception {
        String fileName = "planets-and-temperatures.csv";
        // Read file
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        URI fileUri = loader.getResource(fileName).toURI();
        SplitDataPlanetStats result = classUnderTest.analyzeFile(fileName);

        assertThat(result, allOf(
                hasProperty("coolAverageInKelvin"),
                hasProperty("hotAverageInKelvin")
        ));
        assertThat(result.getCoolAverageInKelvin().doubleValue(), is(not(0.0)));
        assertThat(result.getHotAverageInKelvin().doubleValue(), is(not(0.0)));
    }
}
