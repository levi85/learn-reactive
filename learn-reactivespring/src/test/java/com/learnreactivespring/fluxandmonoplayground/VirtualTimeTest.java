package com.learnreactivespring.fluxandmonoplayground;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import reactor.test.scheduler.VirtualTimeScheduler;

import java.time.Duration;

public class VirtualTimeTest {

    @Test
    public void withoutVirtualTimeTest() {

        Flux<String> stringFlux = Flux.just("A", "B", "C", "D", "E")
                .delayElements(Duration.ofSeconds(1));

        StepVerifier.create(stringFlux.log())
                .expectSubscription()
                .expectNext("A", "B", "C", "D", "E")
                .verifyComplete();

    }

    @Test
    public void withVirtualTImeTest() {
        VirtualTimeScheduler.getOrSet();

        Flux<String> stringFlux = Flux.just("A", "B", "C", "D", "E")
                .delayElements(Duration.ofSeconds(1));

        StepVerifier.withVirtualTime(() -> stringFlux.log())
                .expectSubscription()
                .thenAwait(Duration.ofSeconds(7))
                .expectNext("A", "B", "C", "D", "E")
                .verifyComplete();
    }


}
