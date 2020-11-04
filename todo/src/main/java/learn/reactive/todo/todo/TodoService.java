package learn.reactive.todo.todo;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;
import reactor.core.publisher.EmitterProcessor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.FluxSink;

import java.time.Duration;
import java.util.function.Supplier;

@Service
//@AllArgsConstructor
public class TodoService {

    final TodoRepository todoRepository;
    EmitterProcessor<Todo> emitterProcessor = EmitterProcessor.create();


    @Autowired
    EmitterProcessor<String> stringEmitterProcessor;

    Sinks stringSinks = Sinks.many().multicast().onBackpressureBuffer();


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


}
