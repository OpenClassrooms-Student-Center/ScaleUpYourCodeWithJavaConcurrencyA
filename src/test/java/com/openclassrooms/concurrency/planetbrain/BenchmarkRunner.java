package com.openclassrooms.concurrency.planetbrain;

import com.openclassrooms.concurrency.planetbrain.multiprocess.app.PlanetTemperatureAnalyzer;
import com.openclassrooms.concurrency.planetbrain.multiprocess.app.PlanetTemperatureAnalyzerParallel;
import com.openclassrooms.concurrency.planetbrain.parallelstreams.app.PlanetAnalyzerUsingParallelStreams;
import com.openclassrooms.concurrency.planetbrain.threads.app.ThreadBasedPlanetAnalyzerApp;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.runner.RunnerException;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Measurement(time=500, timeUnit = TimeUnit.MILLISECONDS)
@Warmup(time = 500, timeUnit = TimeUnit.MILLISECONDS)
@Fork(5)
public class BenchmarkRunner {

    // Benchmarking fixtures
    private static final String[] SINGLE_INPUT_FILE = {"all-planets.csv"};
    private static final String[] SPLIT_PLANETS_FILES = {"first-55820.csv", "last-55820.csv"};


    public static void main(String[] args) throws RunnerException, IOException {
        // delegate to JMH's Main class
        org.openjdk.jmh.Main.main(args);
    }

    @Benchmark
    public void benchmarkSingleProcess() throws Exception {
        PlanetTemperatureAnalyzer.main(SINGLE_INPUT_FILE);
    }

    @Benchmark
    public void benchmarkMultiProcess() throws Exception {
        PlanetTemperatureAnalyzerParallel.main(SPLIT_PLANETS_FILES);
    }

    @Benchmark
    public void benchmarkParallelStream() throws Exception {
        PlanetAnalyzerUsingParallelStreams.main(SINGLE_INPUT_FILE);
    }

    @Benchmark
    public void benchmarkRawThreadsWithFutureTasks() throws Exception {
        ThreadBasedPlanetAnalyzerApp.main(SPLIT_PLANETS_FILES);
    }

    @Benchmark
    public void benchmarkFuturesWithExecutorService() throws Exception {
        ThreadBasedPlanetAnalyzerApp.main(SPLIT_PLANETS_FILES);
    }
}
