package org.example;

import java.time.Clock;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

public class ExpiringCache {
    private final Clock clock;

    private final Map<String, CacheEntry> storage = new HashMap<>();

    private record CacheEntry(String value, Instant expiryDate) {}

    public ExpiringCache(Clock clock) {
        this.clock = clock;
    }

    public ExpiringCache() {
        this(Clock.systemDefaultZone());
    }

    public void put(String key, String value, long ttlMillis) {
        Instant now = Instant.now(clock);
        Instant expiryDate = now.plusMillis(ttlMillis);
        storage.put(key, new CacheEntry(value, expiryDate));
    }

    public String get(String key) {
        CacheEntry entry  = storage.get(key);
        if (entry == null) return null;

        Instant now = Instant.now(clock);

        if (now.isAfter(entry.expiryDate()) || now.equals(entry.expiryDate())) {
            storage.remove(key);
            return null;
        }

        return entry.value();
    }
}
