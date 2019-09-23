package com.openclassrooms.concurrency.planetbrain.model;

import java.math.BigDecimal;

public class PlanetStats {

    private BigDecimal coolAverageInKelvin;
    private BigDecimal hotAverageInKelvin;

    public PlanetStats(BigDecimal coolAverageInKelvin, BigDecimal hotAverage) {
        this.coolAverageInKelvin = coolAverageInKelvin;
        this.hotAverageInKelvin = hotAverage;
    }

    public BigDecimal getCoolAverageInKelvin() {
        return coolAverageInKelvin;
    }

    public BigDecimal getHotAverageInKelvin() {
        return hotAverageInKelvin;
    }
}
