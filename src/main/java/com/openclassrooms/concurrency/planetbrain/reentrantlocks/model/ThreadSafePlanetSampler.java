package com.openclassrooms.concurrency.planetbrain.reentrantlocks.model;

import java.util.concurrent.locks.ReentrantLock;

/**
 * A thread safe sampler which is used to calculate the mean
 * of across a sample of temperatures in Kelvin
 */
public class ThreadSafePlanetSampler {
    private Integer sampleSize = 0;
    private Double temperatureTotal = 0.0;
    private ReentrantLock lock;

    public ThreadSafePlanetSampler(ReentrantLock lock){
        this.lock = lock;
    }

    /**
     * Adds a sample temperature in Kelvin to our sample set
     * @param temperature in kelvin
     */
    public void addSample(Double temperature){
        try {
            lock.lock();
            sampleSize++;
            temperatureTotal += temperature;
        } finally {
            lock.unlock();
        }
    }

    /**
     * Returns the mean based on temperatures sampled to this point
     * @return Mean temperature in Kelvin
     */
    public Double getAverage() {
        try {
            lock.lock();
            return temperatureTotal/sampleSize;
        } finally {
            lock.unlock();
        }
    }
}