package com.openclassrooms.concurrency.planetbrain;

import com.openclassrooms.concurrency.planetbrain.model.PlanetStats;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.core.AllOf.allOf;

@DisplayName("Given that the PlanetBrain app is running")
public class PlanetBrainIntegrationTest {

    // This is the class which does all the work
    PlanetFileAnalyser classUnderTest = new PlanetFileAnalyser();

    @Test
    @DisplayName("as a single process, when provided with a single file then it should calculate two averages")
    public void runSingleProcess_shouldCalculateBothAveragesTemperatures_whenPresentedWithASingleFile() {
        String fileName = "kepler-all-planets.txt";
        PlanetStats result = classUnderTest.calculateAveragesFor(fileName);
        assertThat(result, allOf(
                hasProperty("coolAverageTemperature"),
                hasProperty("hotAverageTemperature")
        ));
        assertThat(result.getCoolAverage().doubleValue(), is(not(0)));
        assertThat(result.getHotAverage().doubleValue(), is(not(0)));
    }
}
