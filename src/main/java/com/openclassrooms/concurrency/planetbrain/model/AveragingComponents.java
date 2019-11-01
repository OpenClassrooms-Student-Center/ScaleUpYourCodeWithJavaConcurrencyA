package com.openclassrooms.concurrency.planetbrain.model;

public class AveragingComponents {
    private Double sampleSize = 0.0;
    private Double sampleSum = 0.0;

    public AveragingComponents(Double sampleSize, Double sampleSum) {
        this.sampleSize = sampleSize;
        this.sampleSum = sampleSum;
    }

    public Double getSampleSize() {
        return sampleSize;
    }

    public void setSampleSize(Double sampleSize) {
        this.sampleSize = sampleSize;
    }

    public Double getSampleSum() {
        return sampleSum;
    }

    public void setSampleSum(Double sampleSum) {
        this.sampleSum = sampleSum;
    }
}
