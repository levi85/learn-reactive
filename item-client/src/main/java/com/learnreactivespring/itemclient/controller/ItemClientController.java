package com.learnreactivespring.itemclient.controller;

import com.learnreactivespring.itemclient.item.Item;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@Slf4j
public class ItemClientController {

    WebClient webClient = WebClient.create("http://localhost:8081");

    @GetMapping("client/retrieve")
    public Flux<Item> getAllItemsUsingRetreive() {
        return webClient.get().uri("/v1/fun/items")
                .retrieve()
                .bodyToFlux(Item.class)
                .log("Items in client project");
    }


    @GetMapping("client/exchange")
    public Flux<Item> getAllItemsUsingExchange() {
        return webClient.get().uri("/v1/fun/items")
                .exchange()
                .flatMapMany(clientResponse -> clientResponse.bodyToFlux(Item.class))
                .log();
    }

    @GetMapping("client/retrieve/one")
    public Mono<Item> getOneItemsUsingRetreive() {
        return webClient.get().uri("/v1/fun/items/{id}", "5f74616c3bd9142a66cd8367")
                .retrieve()
                .bodyToMono(Item.class)
                .log("Items in client project");
    }


    @GetMapping("client/exchange/one")
    public Mono<Item> getOneItemsUsingExchange() {
        return webClient.get().uri("/v1/fun/items/{id}", "5f74616c3bd9142a66cd8367")
                .exchange()
                .flatMap(clientResponse -> clientResponse.bodyToMono(Item.class))
                .log();
    }

    @PostMapping("client/retrieve/post")
    public Mono<Item> postItemUsingRetrieve() {

        Item newItem = new Item(null, "IphoneXII", 5000.00);

        return webClient.post().uri("/v1/fun/items")
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(newItem), Item.class)
                .retrieve()
                .bodyToMono(Item.class)
                .log("Created Item: ");
    }


    @GetMapping("/client/retrieve/error")
    public Flux<Item> errorRetrieve() {

        return webClient.get()
                .uri("/v1/items/runtimeException")
                .retrieve()
                .onStatus(HttpStatus::is5xxServerError, clientResponse -> {

                    Mono<String> errorMono = clientResponse.bodyToMono(String.class);
                    return errorMono.flatMap((errorMessage) -> {
                        log.error("The error Message is : " + errorMessage);
                        throw new RuntimeException(errorMessage);
                    });
                })
                .bodyToFlux(Item.class);
    }

    @GetMapping("/client/exchange/error")
    public Flux<Item> errorExchange() {

        return webClient.get()
                .uri("/v1/items/runtimeException")
                .exchange()
                .flatMapMany(clientResponse -> {
                    if (clientResponse.statusCode().is5xxServerError()) {
                        return clientResponse.bodyToMono(String.class)
                                .flatMap(errorMessage -> {
                                    log.error("Error Message in errorExchange : " + errorMessage);
                                    throw new RuntimeException(errorMessage);
                                });
                    } else {
                        return clientResponse.bodyToFlux(Item.class);
                    }
                }).log();
    }
}