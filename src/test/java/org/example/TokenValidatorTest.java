package org.example;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;


import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;


import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;

public class TokenValidatorTest {

    //1. реальное время
    @Test
    void testWithRealTime() throws InterruptedException {
        TokenValidator validator = new TokenValidator();

        Instant expiryDate = Instant.now().plusMillis(500);

        assertTrue(validator.validate(expiryDate), "Токен должен быть годен");

        Thread.sleep(600);

        assertFalse(validator.validate(expiryDate), "Токен должен протухнуть");

    }

    //2. метод инъекции часов
    @Test
    void testWithClockInjection() {
        Instant fixedTime = Instant.parse("2026-01-01T12:00:00Z");
        Clock fixedClock = Clock.fixed(fixedTime, ZoneId.of("UTC"));

        TokenValidator validator =  new TokenValidator(fixedClock);

        Instant pastExpiry = fixedTime.minusSeconds(10);
        assertFalse(validator.validate(pastExpiry));

        Instant futureExpiry = fixedTime.plusSeconds(10);
        assertTrue(validator.validate(futureExpiry));
    }

    //3. метод статического мокирования
    @Test
    void testWithMockito() {
        TokenValidator validator = new TokenValidator();

        Instant fakeNow = Instant.parse("2026-01-01T12:00:00Z");
        Instant expiryDate = fakeNow.minusSeconds(10);
        try (MockedStatic<Instant> mockedInstant =  Mockito.mockStatic(Instant.class)) {
            mockedInstant.when(() -> Instant.now(any(Clock.class))).thenReturn(fakeNow);
            assertFalse(validator.validate(expiryDate));
        }
    }
}