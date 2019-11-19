package com.openclassrooms.concurrency.planetbrain.reactivejava.subscriber;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.logging.Logger;

public class TemperatureSubscriber implements Subscriber<Double> {
    private static Logger logger = Logger.getLogger(TemperatureSubscriber.class.getName());

    private Subscription subscription;

    @Override
    public void onSubscribe(Subscription subscription) {
        this.subscription = subscription;
    }

    @Override
    public void onNext(Double aDouble) {

    }

    @Override
    public void onError(Throwable t) {

    }

    @Override
    public void onComplete() {

    }
}
