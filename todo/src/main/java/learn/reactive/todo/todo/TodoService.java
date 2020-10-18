package learn.reactive.todo.todo;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@AllArgsConstructor
public class TodoService {

    final TodoRepository todoRepository;

    public Mono<Todo> updateTodo(Todo reqUpdateTodo) {

        return todoRepository.save(reqUpdateTodo);

    }
}
