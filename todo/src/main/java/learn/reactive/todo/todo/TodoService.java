package learn.reactive.todo.todo;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;
import reactor.core.publisher.EmitterProcessor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.function.Supplier;

@Service
//@AllArgsConstructor
public class TodoService {

    final TodoRepository todoRepository;
    EmitterProcessor<Todo> emitterProcessor = EmitterProcessor.create();
    EmitterProcessor<String> stringEmitterProcessor = EmitterProcessor.create();


    public TodoService(TodoRepository todoRepository) {
        this.todoRepository = todoRepository;
    }

    public Mono<Todo> updateTodo(Todo reqUpdateTodo) {

        return todoRepository.save(reqUpdateTodo);

    }


    public Mono<Todo> createTodo(Todo newItem) {

        Mono<Todo> savedTodo = todoRepository.save(newItem);

        return savedTodo
                .doOnNext(todo -> emitterProcessor.onNext(todo));
    }

    @Bean
    public Supplier<Flux<Todo>> broadcastNewTodo() {
        return () -> Flux.just(new Todo(1, "dummy desc", "dummy details", false));
    }

    public Flux<String> getMessage() {

        stringEmitterProcessor.onNext("HelloWorld");

        return Flux.just("HelloWorld");
    }

    @Bean
    public Supplier<Flux<String>> broadcast() {
        return () -> {

            return stringEmitterProcessor;

//            return Flux.interval(Duration.ofSeconds(1)).map(value -> "Hello " + value);
        };
    }

}
