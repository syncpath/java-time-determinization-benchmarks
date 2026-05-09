package org.example;

import java.util.function.Supplier;

public class RetryService {
    private int maxAttempts;
    long delayMillis;
    Sleeper sleeper;

    public RetryService(int maxAttempts, long dealayMillis, Sleeper sleeper) {
        this.maxAttempts = maxAttempts;
        this.delayMillis = dealayMillis;
        this.sleeper = sleeper;
    }

    public String execute(Supplier<String> action) {
        for (int attempt = 1; attempt  <= maxAttempts; attempt++) {
            try {
                return action.get();
            } catch (RuntimeException e) {
                if (attempt == maxAttempts) {
                    throw e;
                }
                try {
                    sleeper.sleep(delayMillis);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("Retry interrupted", ie);
                }
            }
        }
        return null;
    }

}
