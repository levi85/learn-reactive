package com.learnreactivespring.initialize;

import com.learnreactivespring.document.Item;
import com.learnreactivespring.repository.ItemReactiveRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Profile("!test")
@Component
public class ItemDataInitializer implements CommandLineRunner {

    @Autowired
    ItemReactiveRepo itemReactiveRepo;

    @Override
    public void run(String... args) throws Exception {
        initialDataSetup();

    }

    public List<Item> data() {
        return Arrays.asList(new Item(null, "Samsung TV", 400.00),
                new Item(null, "LG TV", 420.00),
                new Item(null, "Apple Watch", 299.99),
                new Item(null, "Beats Headphones", 149.99),
                new Item(null, "Bose Headphones", 249.99));

    }

    private void initialDataSetup() {
        itemReactiveRepo.deleteAll()
                .thenMany(Flux.fromIterable(data()))
                .flatMap(item -> itemReactiveRepo.save(item))
                .log("Data Init : ")
                .subscribe();
    }
}
