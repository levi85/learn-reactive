package learn.reactive.todo.todo;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;

@Configuration
public class TodoRouter {
    public static final String TODO_ENDPOINT_V1 = "/v1/todos";

    @Bean
    public RouterFunction<ServerResponse> todoRoute(TodoHandler todoHandler) {
        return RouterFunctions
                .route(GET(TodoRouter.TODO_ENDPOINT_V1).and(accept(MediaType.APPLICATION_JSON)), todoHandler::getAllItems)
                .andRoute(POST(TodoRouter.TODO_ENDPOINT_V1).and(accept(MediaType.APPLICATION_JSON)), todoHandler::newTodo);
    }


}