package com.learnreactivespring.controller;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import reactor.test.scheduler.VirtualTimeScheduler;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@WebFluxTest
public class FluxAndMonoControllerTest {

    @Autowired
    WebTestClient webTestClient;

    @Test
    public void flux_approach1_StepVerifier() {

        VirtualTimeScheduler vts = VirtualTimeScheduler.create();

        vts.advanceTimeBy(Duration.ofSeconds(4));

        Flux<Integer> integerFlux = webTestClient.get()
                .uri("/flux")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .returnResult(Integer.class)
                .getResponseBody();


        StepVerifier.create(integerFlux.log())
                .expectSubscription()
                .expectNext(1, 2, 3, 4)
                .verifyComplete();
    }

    @Test
    public void flux_approach2_webTestClient() {
        webTestClient.get().uri("/flux")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(Integer.class)
                .hasSize(4);
    }

    @Test
    public void flux_appoarch3_assertJLibrary() {

        List<Integer> expectedResult = Arrays.asList(1, 2, 3, 4);

       EntityExchangeResult entityExchangeResult = webTestClient.get().uri("/flux")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Integer.class)
                .returnResult();


        assertThat(entityExchangeResult.getResponseBody()).isEqualTo(expectedResult);

    }

    @Test
    public void flux_appoarch4_consumeWith() {

        webTestClient.get().uri("/flux")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Integer.class)
                .consumeWith(response -> {
                    assertThat(response.getResponseBody())
                            .isEqualTo(Arrays.asList(1, 2, 3, 4));
                });
    }

    @Test
    public void flux_approach5_stream() {

        Flux<Integer> integerFluxResult = webTestClient.get().uri("/fluxstream")
                .accept(MediaType.APPLICATION_STREAM_JSON)
                .exchange()
                .expectStatus().isOk()
                .returnResult(Integer.class)
                .getResponseBody();


        StepVerifier.create(integerFluxResult)
                .expectSubscription()
                .expectNext(0, 1, 2, 3)
                .thenCancel()
                .verify();
    }

    @Test
    public void mono() {

        webTestClient.get().uri("/mono")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .consumeWith(response -> {
                    assertThat(response.getResponseBody()).isEqualToIgnoringCase("Hello");
                });
    }

}
