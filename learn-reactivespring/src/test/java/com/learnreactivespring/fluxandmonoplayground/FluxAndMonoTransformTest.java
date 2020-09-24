package com.learnreactivespring.fluxandmonoplayground;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

import static reactor.core.scheduler.Schedulers.parallel;

public class FluxAndMonoTransformTest {

    List<String> names = Arrays.asList("adam", "anna", "jack", "jenny");

    @Test
    public void transformUsingMap() {

        Flux<String> namesFlux = Flux.fromIterable(names)
                .map(s -> s.toUpperCase())
                .log();

        StepVerifier.create(namesFlux)
                .expectNext("ADAM", "ANNA", "JACK", "JENNY")
                .verifyComplete();
    }

    @Test
    public void transformUsingMap_length() {

        Flux<Integer> namesFlux = Flux.fromIterable(names)
                .map(s -> s.length())
                .log();

        StepVerifier.create(namesFlux)
                .expectNext(4,4,4,5)
                .verifyComplete();
    }

    @Test
    public void transformUsingMap_repeat() {

        Flux<Integer> namesFlux = Flux.fromIterable(names)
                .map(s -> s.length())
                .repeat(1)
                .log();

        StepVerifier.create(namesFlux)
                .expectNext(4,4,4,5, 4,4,4,5)
                .verifyComplete();
    }

    @Test
    public void transformUsingMap_Filter() {

        Flux<String> namesFlux = Flux.fromIterable(names)
                .filter(s -> s.length() >4)
                .map(s -> s.toUpperCase())
                .log();

        StepVerifier.create(namesFlux)
                .expectNext("JENNY")
                .verifyComplete();
    }

    @Test
    public void transformUsingFlatMap() {
        Flux<String> names = Flux.fromIterable(Arrays.asList("A", "B", "C", "D", "E", "F"))
                .flatMap(s -> {
                    return Flux.fromIterable(convertToList(s));
                })
                .log();


        StepVerifier.create(names)
                .expectNextCount(12)
                .verifyComplete();
    }

    private List<String> convertToList(String s) {

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return Arrays.asList(s, "new Value");
    }

    @Test
    public void transformUsingFlatMap_usingParallel() {
        Flux<String> names = Flux.fromIterable(Arrays.asList("A", "B", "C", "D", "E", "F"))
                .window(2)
                .flatMap(s -> {
                    return s.map(this::convertToList).subscribeOn(parallel());
                })
                .flatMap(s -> Flux.fromIterable(s))
                .log();


        StepVerifier.create(names)
                .expectNextCount(12)
                .verifyComplete();
    }

    @Test
    public void transformUsingFlatMap_usingParallel_maintain_order() {
        Flux<String> names = Flux.fromIterable(Arrays.asList("A", "B", "C", "D", "E", "F"))
                .window(2)
                .flatMapSequential(s -> {
                    return s.map(this::convertToList).subscribeOn(parallel());
                })
                .flatMap(s -> Flux.fromIterable(s))
                .log();


        StepVerifier.create(names)
                .expectNextCount(12)
                .verifyComplete();
    }
}
