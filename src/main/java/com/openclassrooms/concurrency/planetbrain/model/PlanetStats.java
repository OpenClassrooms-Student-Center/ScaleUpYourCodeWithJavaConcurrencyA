package com.openclassrooms.concurrency.planetbrain.model;

import java.math.BigDecimal;

public class PlanetStats {

    private BigDecimal coolAverage;
    private BigDecimal hotAverage;

    public PlanetStats(BigDecimal coolAverage, BigDecimal hotAverage) {
        this.coolAverage = coolAverage;
        this.hotAverage = hotAverage;
    }

    public BigDecimal getCoolAverage() {
        return hotAverage;
    }

    public BigDecimal getHotAverage() {
        return hotAverage;
    }
}
