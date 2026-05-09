package org.example;

import org.openjdk.jmh.annotations.*;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = 2, time = 1)
@Measurement(iterations = 25, time = 1)
@State(Scope.Thread)
@Fork(1)
public class AsyncProcessorBenchmark {

    private AsyncProcessor processor;

    @Setup(Level.Iteration)
    public void setUp() {
        processor = new AsyncProcessor();
    }

    // 1. реальное время
    @Benchmark
    public void testRealTime() {
        StepVerifier.create(processor.processWithDelay("hello reactor", 1000))
                .expectNext("HELLO REACTOR")
                .verifyComplete();
    }

    // 2. виртуальное время
    @Benchmark
    public void testVirtualTimeInjection() {
        StepVerifier.withVirtualTime(() -> processor.processWithDelay("hello virtual time", 1000))
                .thenAwait(Duration.ofSeconds(1))
                .expectNext("HELLO VIRTUAL TIME")
                .verifyComplete();
    }
}
