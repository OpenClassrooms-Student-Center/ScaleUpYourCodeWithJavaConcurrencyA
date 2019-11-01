package com.openclassrooms.concurrency.planetbrain.forkjoin.app.task;

import com.openclassrooms.concurrency.planetbrain.model.AveragingComponents;
import com.openclassrooms.concurrency.planetbrain.threads.service.ThreadBasedPlanetFileAnalyzer;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.RecursiveTask;

public class TemperatureSummingTask extends RecursiveTask<AveragingComponents> {

    private List<Path> files;

    // TODO: Make this injectable (decouple)
    private ThreadBasedPlanetFileAnalyzer fileAnalyzer = new ThreadBasedPlanetFileAnalyzer();

    public TemperatureSummingTask(List<Path> files) {
        this.files = files;
    }

    @Override
    protected AveragingComponents compute() {
        AveragingComponents averagingComponents;
        // 1. Base Case
        if (files.size() == 1) {
            try {
                averagingComponents = fileAnalyzer.averagingComponents(files.get(0));
            } catch (IOException e) {
                // TODO handle errors
                e.printStackTrace();
                return new AveragingComponents(0.0, 0.0);
            }
        } else {
            // 2. General Case
            int midPoint = files.size()/2;
            TemperatureSummingTask left = new TemperatureSummingTask(files.subList(0, midPoint));
            TemperatureSummingTask right = new TemperatureSummingTask(files.subList(midPoint, files.size()));

            // asynchronously work out the left side
            left.fork();

            // Work out the right hand side in this thread.
            AveragingComponents rightResult = right.compute();

            // Get back the left side when it's ready
            AveragingComponents leftResult = left.join();

            // Calculate our combined result
            averagingComponents = new AveragingComponents(
                    rightResult.getSampleSize() + leftResult.getSampleSize(),
                    rightResult.getSampleSum() + leftResult.getSampleSum()
            );
        }
        return averagingComponents;
    }
}
