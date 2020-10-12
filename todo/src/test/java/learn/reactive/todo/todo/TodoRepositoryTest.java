package learn.reactive.todo.todo;

import io.r2dbc.spi.ConnectionFactory;
import learn.reactive.todo.todo.mock.TodoStubFactory;
import lombok.AllArgsConstructor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import org.springframework.data.r2dbc.core.DatabaseClient;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;

@DataR2dbcTest
@ExtendWith(SpringExtension.class)
public class TodoRepositoryTest {

    @Autowired
    private  DatabaseClient databaseClient;

    @Autowired
    private  TodoRepository todoRepository;

    @Test
    public void saveTodo_givenNew_shouldCreateNew() {
        //arrange
        Todo todo = TodoStubFactory.Buy_Groceries();

        //act
        Mono<Todo> savedTodo = Mono.just(todo)
                .flatMap(todoRepository::save)
                .log();

        //assert
        StepVerifier.create(savedTodo)
                .expectSubscription()
                .assertNext(todoResult -> assertThat(todoResult).isEqualToIgnoringGivenFields(TodoStubFactory.Buy_Groceries(), "id"))
                .verifyComplete();
    }

    @Test
    public void saveTodo_givenExistingTodo_shouldUpdate() {
        //arrange
        Todo todo = TodoStubFactory.Buy_Groceries();
        todo.setId(4);
        todo.setDetails("Oat Milk, Yogurt");

        //act
        Mono<Todo> savedTodo = Mono.just(todo)
                .flatMap(todoRepository::save)
                .log();

        //assert
        StepVerifier.create(savedTodo)
                .expectSubscription()
                .assertNext(todoResult -> assertThat(todoResult).isEqualToComparingFieldByField(todo))
                .verifyComplete();
    }

}
