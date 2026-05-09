package org.example;

import org.openjdk.jmh.annotations.*;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import static org.mockito.ArgumentMatchers.any;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = 2, time = 1)
@Measurement(iterations = 25, time = 1)
@State(Scope.Thread)
@Fork(1)
public class TokenValidatorBenchmark {

    private TokenValidator standardValidator;
    private TokenValidator injectedValidator;
    private Instant fixedTime;
    private Clock fixedClock;
    private Instant realTimeExpiryDate;

    // этот метод выполняется один раз перед каждым циклом замеров
    @Setup(Level.Iteration)
    public void setUp() {
        standardValidator = new TokenValidator();

        fixedTime = Instant.parse("2026-01-01T12:00:00Z");
        fixedClock = Clock.fixed(fixedTime, ZoneId.of("UTC"));
        injectedValidator = new TokenValidator(fixedClock);

        //даем токену запас времени
        realTimeExpiryDate = Instant.now().plusSeconds(60);
    }

    // 1. реальное время
    @Benchmark
    public boolean testRealTime() throws InterruptedException {
        //ожидание бизнес процесса
        Thread.sleep(100);
        return standardValidator.validate(realTimeExpiryDate);
    }

    // 2. метод инъекции
    @Benchmark
    public boolean testClockInjection() {
        Instant futureExpiry = fixedTime.plusSeconds(10);
        // Выполняется мгновенно, без создания новых объектов внутри теста
        return injectedValidator.validate(futureExpiry);
    }

    // 3. статическое мокирование
    @Benchmark
    public boolean testMockito() {
        TokenValidator validator = new TokenValidator();
        Instant expiryDate = fixedTime.minusSeconds(10);

        try (MockedStatic<Instant> mockedInstant = Mockito.mockStatic(Instant.class)) {
            mockedInstant.when(() -> Instant.now(any(Clock.class))).thenReturn(fixedTime);
            return validator.validate(expiryDate);
        }
    }
}
