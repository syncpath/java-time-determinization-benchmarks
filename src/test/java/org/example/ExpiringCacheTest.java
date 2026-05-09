package org.example;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
public class ExpiringCacheTest {
    //1. реальное время
    @Test
    void testCacheWithRealTime() throws InterruptedException {
        ExpiringCache cache = new ExpiringCache();

        cache.put("user_1", "John Doe", 300);

        assertEquals("John Doe", cache.get("user_1"));

        Thread.sleep(350);

        assertNull(cache.get("user_1"), "Кэш должен был отлкючиться");
    }

    //2. метод инъекции часов
    @Test
    void testCacheWithClockInjection() {
        Instant startTime = Instant.parse("2026-01-01T10:00:00Z");

        ExpiringCache cache = new ExpiringCache(Clock.fixed(startTime, ZoneId.of("UTC")));
        cache.put("token", "secret_123", 5000);

        Instant futureTime = startTime.plusSeconds(6);
        Clock futureClock = Clock.fixed(futureTime, ZoneId.of("UTC"));
    }

    //3. метод статического мокирования
    @Test
    void testCacheWithMockito() {
        ExpiringCache cache = new ExpiringCache();

        Instant startTime = Instant.parse("2026-01-01T10:00:00Z");
        Instant futureTime = startTime.plusSeconds(6);

        try (MockedStatic<Instant> mockedInstant = Mockito.mockStatic(Instant.class, Mockito.CALLS_REAL_METHODS)) {
            mockedInstant.when(() -> Instant.now(any(Clock.class)))
                    .thenReturn(startTime, futureTime);

            cache.put("token", "secret_123", 5000);

            assertNull(cache.get("token"), "Кэш должен вернуть null, так как время вышло");
        }
    }
}

