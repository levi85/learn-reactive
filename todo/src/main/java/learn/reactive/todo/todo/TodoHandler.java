package learn.reactive.todo.todo;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@AllArgsConstructor
@Component
public class TodoHandler {

    private final TodoRepository todoRepository;

    public Mono<ServerResponse> getAllItems(ServerRequest serverRequest) {

        return  ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(todoRepository.findAll(), Todo.class);


    }

    public Mono<ServerResponse> newTodo(ServerRequest serverRequest) {


        Mono<Todo> newTodo = serverRequest.bodyToMono(Todo.class);

        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(newTodo.flatMap(todo -> todoRepository.save(todo)), Todo.class);
    }
}