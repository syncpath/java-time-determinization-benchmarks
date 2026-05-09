package org.example;

import org.openjdk.jmh.annotations.*;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.concurrent.TimeUnit;

import static org.mockito.ArgumentMatchers.any;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = 2, time = 1)
@Measurement(iterations = 25, time = 1)
@State(Scope.Thread)
@Fork(1)
public class ExpiringCacheBenchmark {

    private ExpiringCache standardCache;
    private ExpiringCache injectedCache;
    private Instant startTime;

    @Setup(Level.Iteration)
    public void setUp() {
        standardCache = new ExpiringCache();
        startTime = Instant.parse("2026-01-01T10:00:00Z");
        injectedCache = new ExpiringCache(Clock.fixed(startTime, ZoneId.of("UTC")));
    }

    // 1. реальное время
    @Benchmark
    public String testRealTime() throws InterruptedException {
        standardCache.put("key_real", "value", 100);
        Thread.sleep(100);
        return standardCache.get("key_real");
    }

    // 2. инъекция зависимостей
    @Benchmark
    public String testClockInjection() {
        // cимулируем истекшее время
        injectedCache.put("key_injected", "value", -1000);
        return injectedCache.get("key_injected");
    }

    // 3. статическое мокирование (Mockito)
    @Benchmark
    public String testMockito() {
        ExpiringCache mockCache = new ExpiringCache();
        Instant futureTime = startTime.plusSeconds(10);

        try (MockedStatic<Instant> mockedInstant = Mockito.mockStatic(Instant.class, Mockito.CALLS_REAL_METHODS)) {
            mockedInstant.when(() -> Instant.now(any(Clock.class)))
                    .thenReturn(startTime, futureTime);

            mockCache.put("key_mock", "value", 5000);
            return mockCache.get("key_mock");
        }
    }
}