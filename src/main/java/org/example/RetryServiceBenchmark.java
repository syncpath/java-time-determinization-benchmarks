package org.example;

import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = 2, time = 1)
@Measurement(iterations = 25, time = 1)
@State(Scope.Thread)
@Fork(1)
public class RetryServiceBenchmark {

    private RetryService realRetryService;
    private RetryService fastRetryService;
    private Supplier<String> failingAction;

    @Setup(Level.Iteration)
    public void setUp() {
        // слипер с реальной задержкой
        Sleeper realSleeper = millis -> {
            try {
                Thread.sleep(millis);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        };

        //заглушка
        Sleeper fastSleeper = millis -> {};

        //2 попытки по 500 мс
        realRetryService = new RetryService(2, 500, realSleeper);
        fastRetryService = new RetryService(2, 500, fastSleeper);

        //действие, которое всегда падает
        failingAction = () -> {
            throw new RuntimeException("Simulated failure");
        };
    }

    // 1. реальное время
    @Benchmark
    public String testRealTime() {
        try {
            return realRetryService.execute(failingAction);
        } catch (RuntimeException e) {
            return "failed";
        }
    }

    // 2. инъекция Sleeper
    @Benchmark
    public String testSleeperInjection() {
        try {
            return fastRetryService.execute(failingAction);
        } catch (RuntimeException e) {
            return "failed";
        }
    }
}
