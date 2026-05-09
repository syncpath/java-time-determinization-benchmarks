package org.example;

import java.time.Clock;
import java.time.Instant;

public class TokenValidator {
    private final Clock clock;

    public TokenValidator(Clock clock) {
        this.clock = clock;
    }

    public TokenValidator() {
        this(Clock.systemDefaultZone());
    }

    public boolean validate(Instant expiryDate) {
        if (expiryDate == null) return false;

        Instant now = Instant.now(clock);
        return now.isBefore(expiryDate);
    }
}
