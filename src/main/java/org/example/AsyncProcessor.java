package org.example;

import reactor.core.publisher.Mono;
import java.time.Duration;

public class AsyncProcessor {
    public Mono<String> processWithDelay(String input, long delayMillis) {
        return Mono.just(input)
                .delayElement(Duration.ofMillis(delayMillis))
                .map(String::toUpperCase);
    }
}
