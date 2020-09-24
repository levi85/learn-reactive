package com.learnreactivespring.repository;

import com.learnreactivespring.document.Item;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

@DataMongoTest
@RunWith(SpringRunner.class)
public class ItemReactiveRepoTest {

    @Autowired
    ItemReactiveRepo itemReactiveRepo;

    List<Item> itemList = Arrays.asList(new Item(null, "Samsung TV", 400.00),
            new Item(null, "LG TV", 420.00),
            new Item(null, "Apple Watch", 299.99),
            new Item(null, "Beats Headphones", 149.99),
            new Item("ABC", "Bose Headphones", 149.99));

    @Before
    public void setUp() {
        itemReactiveRepo.deleteAll()
                .thenMany(Flux.fromIterable(itemList))
                .flatMap(itemReactiveRepo::save)
                .doOnNext(item -> {
                    System.out.println("Inserted Item is : " + item);
                })
                .blockLast(); //wait all the items are saved before proceeding, not recommended for production code
    }

    @Test
    public void getAllItems() {
        StepVerifier.create(itemReactiveRepo.findAll())
                .expectSubscription()
                .expectNextCount(4)
                .verifyComplete();
    }

    @Test
    public void getItemById() {
        StepVerifier.create(itemReactiveRepo.findById("ABC"))
                .expectSubscription()
                .expectNextMatches(item -> item.getDescription().equals("Bose Headphones"))
                .verifyComplete();
    }

    @Test
    public void findItemByDescription() {
        StepVerifier.create(itemReactiveRepo.findByDescription("Bose Headphones").log("findItemByDesc"))
                .expectSubscription()
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    public void saveItem() {

        Item item = new Item(null, "Google Home Mini", 50.00);

        Mono<Item> savedItem = itemReactiveRepo.save(item);

        StepVerifier.create(savedItem.log())
                .expectSubscription()
                .expectNextMatches(testItem -> {
                    return testItem.getDescription().equals("Google Home Mini");
                })
                .verifyComplete();
    }

    @Test
    public void updateItem() {
        Flux<Item> updateItem = itemReactiveRepo.findByDescription("Beats Headphones")
                .map(item -> {
                    item.setPrice(138.80);
                    return item;
                })
                .flatMap(item -> itemReactiveRepo.save(item))
                .log();

        StepVerifier.create(updateItem)
                .expectSubscription()
                .expectNextMatches(item -> item.getDescription().equals("Beats Headphones") && item.getPrice() == 138.80)
                .verifyComplete();

    }

    @Test
    public void deleteItemByDescription() {
        Flux<Void> deleteItems = itemReactiveRepo.findByDescription("Beats Headphones")
                .map(item -> item.getId())
                .flatMap(itemId -> itemReactiveRepo.deleteById(itemId))
                .log();


        StepVerifier.create(deleteItems)
                .expectSubscription()
                .verifyComplete();


        StepVerifier.create(itemReactiveRepo.findAll().log("Listed Items: "))
                .expectSubscription()
                .expectNextCount(4)
                .verifyComplete();
    }

    @Test
    public void deleteItem() {
        Mono<Void> deleteItem = itemReactiveRepo.findById("ABC")
                .flatMap(item -> itemReactiveRepo.delete(item))
                .log();

        StepVerifier.create(deleteItem)
                .expectSubscription()
                .verifyComplete();

        StepVerifier.create(itemReactiveRepo.findAll().log("List items: "))
                .expectSubscription()
                .expectNextCount(4)
                .verifyComplete();


    }
}
