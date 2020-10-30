package learn.reactive.todo.todo;

import learn.reactive.todo.todo.exception.InvalidTodoItemException;
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

    private final TodoService todoService;

    public Mono<ServerResponse> getAllItems(ServerRequest serverRequest) {

        return  ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(todoRepository.findAll(), Todo.class);


    }

    public Mono<ServerResponse> newTodo(ServerRequest serverRequest) {


        Mono<Todo> newTodo = serverRequest.bodyToMono(Todo.class);

        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(newTodo
                        .map(todo -> {
                            if (todo.isDone() == true) {
                                throw new InvalidTodoItemException();
                            }
                            return todo;
                        })
                        .flatMap(todo -> todoRepository.save(todo)), Todo.class);
    }

    public Mono<ServerResponse> updateTodo(ServerRequest serverRequest) {

        Mono<Todo> reqTodo = serverRequest.bodyToMono(Todo.class);


        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(reqTodo.flatMap(todo -> todoService.updateTodo(todo)), Todo.class);
    }

    public Mono<ServerResponse> createMessage(ServerRequest serverRequest) {

        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(todoService.getMessage(), String.class);
    }
}
