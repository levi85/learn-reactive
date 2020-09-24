package com.learnreactivespring.router;

import com.learnreactivespring.handler.SampleHandlerFunction;
import lombok.extern.log4j.Log4j;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;

@Log4j2
@Configuration
public class RouterFunctionConfig {

    @Bean
    public RouterFunction<ServerResponse> route(SampleHandlerFunction handlerFunction) {
        log.info("The number of procesor is " + Runtime.getRuntime().availableProcessors());
        return RouterFunctions
                .route(GET("/functional/flux").and(accept(MediaType.APPLICATION_JSON)), handlerFunction::flux)
                .andRoute(GET("/functional/mono").and(accept(MediaType.APPLICATION_JSON)), handlerFunction::mono);
    }


    @Bean
    public RouterFunction<ServerResponse> monoRoute(SampleHandlerFunction handlerFunction) {
        return RouterFunctions
                .route(GET("/special/mono").and(accept(MediaType.APPLICATION_JSON)), handlerFunction::mono);
    }

}
