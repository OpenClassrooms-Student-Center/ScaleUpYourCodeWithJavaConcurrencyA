package com.openclassrooms.concurrency.planetbrain;

import com.openclassrooms.concurrency.planetbrain.atomic.app.AtomicBasedPlanetAnalyzerApp;
import com.openclassrooms.concurrency.planetbrain.blockingqueues.app.BlockingQueuePlanetTemperatureApp;
import com.openclassrooms.concurrency.planetbrain.completablefutures.app.CompletableFuturePlanetAnalyzerApp;
import com.openclassrooms.concurrency.planetbrain.concurrenthashmap.app.ThreadSafeMapAnalyzerApp;
import com.openclassrooms.concurrency.planetbrain.countdownlatches.app.CountdownLatchPlanetAnalyzerApp;
import com.openclassrooms.concurrency.planetbrain.forkjoin.app.ForkJoinPlanetAnalyzerApp;
import com.openclassrooms.concurrency.planetbrain.futures.app.FutureBasedPlanetAnalyzerApp;
import com.openclassrooms.concurrency.planetbrain.multiprocess.app.PlanetTemperatureAnalyzer;
import com.openclassrooms.concurrency.planetbrain.multiprocess.app.PlanetTemperatureAnalyzerParallel;
import com.openclassrooms.concurrency.planetbrain.parallelstreams.app.PlanetAnalyzerUsingParallelStreams;
import com.openclassrooms.concurrency.planetbrain.reentrantlocks.app.ReentrantLockBasedPlanetAnalyzerApp;
import com.openclassrooms.concurrency.planetbrain.semaphores.app.SemaphorePlanetAnalyzerApp;
import com.openclassrooms.concurrency.planetbrain.threads.app.ThreadBasedPlanetAnalyzerApp;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.runner.RunnerException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Measurement(time=1500, timeUnit = TimeUnit.MILLISECONDS)
@Warmup(time = 1500, timeUnit = TimeUnit.MILLISECONDS)
@Fork(1)
public class BenchmarkRunner {

    // Benchmarking fixtures
    private static final String[] SINGLE_INPUT_FILE = {"all-planets.csv"};
    private static final String[] SPLIT_PLANETS_FILES = {"first-55820.csv", "last-55820.csv"};
    private static final String[] DIRECTORY_WITH_FILES = {"lots-of-inputs"};
    private static final String[] DIRECTORY_WITH_MANY_MORE_FILES = {"even-more-inputs"};

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
        FutureBasedPlanetAnalyzerApp.main(SPLIT_PLANETS_FILES);
    }

    @Benchmark
    public void benchmarkAtomicsWithFutures() throws Exception {
        AtomicBasedPlanetAnalyzerApp.main(SPLIT_PLANETS_FILES);
    }

    @Benchmark
    public void benchmarkFuturesWithReentrantLocks() throws Exception {
        ReentrantLockBasedPlanetAnalyzerApp.main(SPLIT_PLANETS_FILES);
    }

    @Benchmark
    public void benchmarkFuturesWithEightSemaphores() throws Exception {
        SemaphorePlanetAnalyzerApp.main(DIRECTORY_WITH_FILES);
    }

    @Benchmark
    public void benchmarkFuturesWithCountdownLatches() throws Exception {
        CountdownLatchPlanetAnalyzerApp.main(DIRECTORY_WITH_FILES);
    }

    @Benchmark
    public void benchmarkCompletableFutures() throws Exception {
        CompletableFuturePlanetAnalyzerApp.main(DIRECTORY_WITH_FILES);
    }

    @Benchmark
    public void benchmarkForkJoinRecursiveTasks() throws Exception {
        ForkJoinPlanetAnalyzerApp.main(DIRECTORY_WITH_FILES);
    }


    @Benchmark
    public void benchmarkBlockingQueue() throws Exception {
        BlockingQueuePlanetTemperatureApp.main(DIRECTORY_WITH_MANY_MORE_FILES);
    }

    @Benchmark
    public void benchmarkFuturesAndConcurrentHashMap() throws Exception {
        List<String> args = new ArrayList<String>(Arrays.asList(DIRECTORY_WITH_MANY_MORE_FILES));
        args.add("CONCURRENT_HASHMAP");
        ThreadSafeMapAnalyzerApp.main(args.toArray(new String[]{}));
    }

    @Benchmark
    public void benchmarkFuturesAndSynchronizedMap() throws Exception {
        List<String> args = new ArrayList<String>(Arrays.asList(DIRECTORY_WITH_MANY_MORE_FILES));
        args.add("SYNCHRONIZED_HASHMAP");
        ThreadSafeMapAnalyzerApp.main(args.toArray(new String[]{}));
    }

}
