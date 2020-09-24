package com.learnreactivespring.router;

import com.learnreactivespring.constants.ItemConstants;
import com.learnreactivespring.handler.ItemsHandler;
import com.mongodb.internal.connection.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerRequestExtensionsKt;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;

@Configuration
public class ItemsRouter {
    @Bean
    public RouterFunction<ServerResponse> itemsRoute(ItemsHandler itemsHandler) {

        return RouterFunctions
                .route(GET(ItemConstants.ITEM_FUNCTIONAL_END_POINT_V1).and(accept(MediaType.APPLICATION_JSON)),
                        request -> itemsHandler.getAllItems(request));
    }

}
