package com.learnreactivespring.handler;

import com.learnreactivespring.document.Item;
import com.learnreactivespring.repository.ItemReactiveRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerRequestExtensionsKt;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
public class ItemsHandler {
    @Autowired
    ItemReactiveRepo itemReactiveRepo;

    public Mono<ServerResponse> getAllItems(ServerRequest serverRequest) {
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(itemReactiveRepo.findAll(), Item.class);
    }

    public Mono<ServerResponse> getOneItem(ServerRequest serverRequest) {
        String id = serverRequest.pathVariable("id");
        Mono<Item> itemMono = itemReactiveRepo.findById(id);

//        return ServerResponse.ok()
//                .contentType(MediaType.APPLICATION_JSON)
//                .body(itemMono, Item.class).switchIfEmpty(ServerResponse.notFound().build());

        return itemMono.flatMap(item -> ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(item)))
                .switchIfEmpty(ServerResponse.notFound().build());
    }


    public Mono<ServerResponse> createItem(ServerRequest request) {

        Mono<Item> itemMono = request.bodyToMono(Item.class);

        return itemMono.flatMap(item ->
            ServerResponse.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(itemReactiveRepo.save(item), Item.class));

    }

    public Mono<ServerResponse> deleteItem(ServerRequest serverRequest) {

        String id = serverRequest.pathVariable("id");

        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(itemReactiveRepo.deleteById(id), Void.class);
    }

    public Mono<ServerResponse> updateItem(ServerRequest serverRequest) {

        String itemId = serverRequest.pathVariable("id");

        Mono<Item> itemMono = serverRequest.bodyToMono(Item.class);

        return itemMono.flatMap(requestItem -> {
            return ServerResponse.ok()
                    .body(itemReactiveRepo.findById(itemId)
                            .flatMap(foundItem -> {
                                foundItem.setDescription(requestItem.getDescription());
                                foundItem.setPrice(requestItem.getPrice());
                                return itemReactiveRepo.save(foundItem);
                            }), Item.class);
        }).switchIfEmpty(ServerResponse.notFound().build());

    }

    public Mono<ServerResponse> runtimeError(ServerRequest serverRequest) {
        throw new RuntimeException("can't run");
    }
}
