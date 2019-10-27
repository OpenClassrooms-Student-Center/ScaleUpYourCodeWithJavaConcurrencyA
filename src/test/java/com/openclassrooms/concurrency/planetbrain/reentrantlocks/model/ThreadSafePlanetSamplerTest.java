package com.openclassrooms.concurrency.planetbrain.reentrantlocks.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.concurrent.locks.ReentrantLock;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("Given that we have a ReentrantLock")
class ThreadSafePlanetSamplerTest {

    @Mock
    ReentrantLock lock;
    @InjectMocks
    ThreadSafePlanetSampler underTest = new ThreadSafePlanetSampler(lock);

    @Test
    @DisplayName("when sampling the lock should lock and unlock")
    public void itShouldUtiliseTheLockWhenSampling() {
        underTest.addSample(1.1);
        underTest.addSample(1.1);
        verify(lock, times(2)).lock();
        verify(lock, times(2)).unlock();
    }
}