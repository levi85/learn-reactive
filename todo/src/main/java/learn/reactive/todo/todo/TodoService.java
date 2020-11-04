package learn.reactive.todo.todo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import reactor.core.publisher.EmitterProcessor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.function.Supplier;

@Service
public class TodoService {

    final TodoRepository todoRepository;
    EmitterProcessor<Todo> todoEmitterProcessor = EmitterProcessor.create();


    @Autowired
    EmitterProcessor<String> stringEmitterProcessor;


    public TodoService(TodoRepository todoRepository) {
        this.todoRepository = todoRepository;
    }

    public Mono<Todo> updateTodo(Todo reqUpdateTodo) {

        return todoRepository.save(reqUpdateTodo);

    }


    public Mono<Todo> createTodo(Todo newItem) {

        Mono<Todo> savedTodo = todoRepository.save(newItem);

        return savedTodo
                .doOnNext(todo -> todoEmitterProcessor.onNext(todo));
    }

    public Flux<Todo> syncTodo() {
        return Flux.range(1, 20)
                .flatMap(value -> todoRepository.save(new Todo(null, "Sample Todo Desc" + value, "some deatils " + value, false))
                        .doOnNext(savedTodo -> todoEmitterProcessor.onNext(savedTodo)))
                .log();
    }

    @Bean
    public Supplier<Flux<Todo>> broadcastTodo() {
        return () -> todoEmitterProcessor;
    }

    @Bean
    public ApplicationRunner smallRunner() {
        return new ApplicationRunner() {
            @Override
            public void run(ApplicationArguments args) throws Exception {
                syncTodo().subscribe();
                todoEmitterProcessor.doOnNext(todo -> System.out.println("Emitter working " + todo.getId())).subscribe();
            }
        };
    }

    public Flux<String> getMessage() {

        stringEmitterProcessor.onNext("HelloWorld");

        return Flux.just("HelloWorld");
    }


}
