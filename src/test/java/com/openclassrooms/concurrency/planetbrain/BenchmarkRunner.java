package com.openclassrooms.concurrency.planetbrain;

import com.openclassrooms.concurrency.planetbrain.multiprocess.app.PlanetTemperatureAnalyzer;
import com.openclassrooms.concurrency.planetbrain.multiprocess.app.PlanetTemperatureAnalyzerParallel;
import com.openclassrooms.concurrency.planetbrain.multiprocess.service.PlanetFileSequentialAnalyser;
import com.openclassrooms.concurrency.planetbrain.parallelstreams.app.PlanetAnalyzerUsingParallelStreams;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.SampleTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Measurement(batchSize = 1, iterations = 5)
@Warmup(batchSize = 1, iterations = 3)
//@State(Scope.Thread)
//@State(Scope.Benchmark)
public class BenchmarkRunner {

    // Benchmarking fixtures
    private static final String ALL_PLANETS_FILE = "all-planets.csv";
    private static final String[] SPLIT_PLANETS_FILES = {"first-55820.csv", "last-55820.csv"};


    public static void main(String[] args) throws RunnerException, IOException {
        String[] jmhArgs = new String[] { ".*" };
        //org.openjdk.jmh.Main.main(jmhArgs);

        Options opt = new OptionsBuilder()
                .include(".*")
                .build();

        new Runner(opt).run();
    }


    //@Fork(jvmArgs = "-Djava.lang.invoke.stringConcat=BC_SB")
    @Benchmark
    public void benchmarkSingleProcess() throws Exception {
        PlanetFileSequentialAnalyser sequentialAnalyzer = new PlanetFileSequentialAnalyser();
        PlanetTemperatureAnalyzer singleProcessAnalyzer = new PlanetTemperatureAnalyzer(sequentialAnalyzer);
        singleProcessAnalyzer.analyzeFile(ALL_PLANETS_FILE);
    }

    @Benchmark
    public void benchmarkMultiProcess() throws Exception {
        PlanetTemperatureAnalyzerParallel.main(SPLIT_PLANETS_FILES);
    }

    @Benchmark
    public void benchmarkParallelStream() {
        PlanetAnalyzerUsingParallelStreams.analyzeFile(ALL_PLANETS_FILE);
    }
}
