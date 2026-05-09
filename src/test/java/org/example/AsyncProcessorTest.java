package org.example;

import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;
import java.time.Duration;

public class AsyncProcessorTest {
    //1. реальное время
    @Test
    void testProcessWithRealTime() {
        AsyncProcessor processor = new AsyncProcessor();

        StepVerifier.create(processor.processWithDelay("hello reactor", 1000))
                .expectNext("HELLO REACTOR")
                .verifyComplete();
    }

    //2. виртуальное время
    @Test
    void testProcessWithVirualTime() {
        AsyncProcessor processor = new AsyncProcessor();

        StepVerifier.withVirtualTime(() -> processor.processWithDelay("hello virtual time", 10000))
                .thenAwait(Duration.ofSeconds(10))
                .expectNext("HELLO VIRTUAL TIME")
                .verifyComplete();
    }
}
