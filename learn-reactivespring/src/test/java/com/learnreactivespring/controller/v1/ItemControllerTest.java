package com.learnreactivespring.controller.v1;

import com.learnreactivespring.constants.ItemConstants;
import com.learnreactivespring.document.Item;
import com.learnreactivespring.repository.ItemReactiveRepo;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@RunWith(SpringRunner.class)
@DirtiesContext
@AutoConfigureWebTestClient
@ActiveProfiles("test")
@Slf4j
public class ItemControllerTest {

    @Autowired
    WebTestClient webTestClient;

    @Autowired
    ItemReactiveRepo itemReactiveRepo;

    @Before
    public void setup() {
        List<Item> data = Arrays.asList(new Item(null, "Samsung TV", 400.00),
                new Item(null, "LG TV", 420.00),
                new Item(null, "Apple Watch", 299.99),
                new Item(null, "Beats Headphones", 149.99),
                new Item("ABC", "Bose Headphones", 249.99));

        itemReactiveRepo.deleteAll()
                .thenMany(Flux.fromIterable(data))
                .flatMap(item -> itemReactiveRepo.save(item))
                .doOnNext(item -> System.out.println("Item inserted : " +item))
                .blockLast();
    }

    @Test
    public void getAllItems() {
        webTestClient.get().uri(ItemConstants.ITEM_END_POINT_V1)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(Item.class)
                .hasSize(5);
    }

    @Test
    public void getAllItems_Approach1(){
        webTestClient.get().uri(ItemConstants.ITEM_END_POINT_V1)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(Item.class)
                .hasSize(5)
                .consumeWith(response -> {
                    List<Item> items = response.getResponseBody();
                    items.forEach(item -> {
                        assertThat(item.getId()).isNotNull();
                    });
                });
    }

    @Test
    public void getAllITems_Approach2() {
        Flux<Item> items = webTestClient.get().uri(ItemConstants.ITEM_END_POINT_V1)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .returnResult(Item.class)
                .getResponseBody();

        StepVerifier.create(items.log("Approach 2 with Flux: "))
                .expectSubscription()
                .expectNextCount(5)
                .verifyComplete();
    }

    @Test
    public void getOneItem() {
        webTestClient.get().uri(ItemConstants.ITEM_END_POINT_V1.concat("/{id}"), "ABC")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.price").isEqualTo(249.99);
    }

    @Test
    public void getOneItem_NotFound() {
        webTestClient.get().uri(ItemConstants.ITEM_END_POINT_V1.concat("/{id}"), "DEF")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    public void createOneItem() {

        Item newItem = new Item(null, "IPHONE XR", 599);

        webTestClient.post().uri(ItemConstants.ITEM_END_POINT_V1)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(newItem), Item.class)
                .exchange()
                .expectBody()
                .jsonPath("$.id").isNotEmpty()
                .jsonPath("$.description").isEqualTo("IPHONE XR")
                .jsonPath("$.price").isEqualTo(599);
    }

    @Test
    public void deleteOneItem() {

        webTestClient.delete().uri(ItemConstants.ITEM_END_POINT_V1.concat("/{id}"), "ABC")
                .exchange()
                .expectStatus().isOk()
                .expectBody(Void.class);
    }
}
