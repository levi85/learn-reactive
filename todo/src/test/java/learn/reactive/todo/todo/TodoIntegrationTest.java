package learn.reactive.todo.todo;

import learn.reactive.todo.todo.exception.InvalidTodoItemException;
import learn.reactive.todo.todo.mock.TodoStubFactory;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@AutoConfigureWebTestClient
@ExtendWith(SpringExtension.class)
@SpringBootTest
@DirtiesContext
public class TodoIntegrationTest {

    @Autowired
    WebTestClient webTestClient;

    @Autowired
    TodoRepository todoRepository;

    @Before
    public void setup() {
        todoRepository.deleteAll().block();
    }


    @Test
    public void getTodoList_shouldReturnAllItems() {
        //arrange
        List<Todo> todoList = Arrays.asList(TodoStubFactory.Buy_Groceries(), TodoStubFactory.Wash_Car());

        Flux.fromIterable(todoList)
                .flatMap(todo -> todoRepository.save(todo))
                .doOnNext(todo -> System.out.println("Todo inserted : " + todo))
                .blockLast();

        //act
        Flux<Todo> todoResult = webTestClient.get().uri(TodoRouter.TODO_ENDPOINT_V1).accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .returnResult(Todo.class)
                .getResponseBody();


        //assert
        StepVerifier.create(todoResult.log())
                .expectSubscription()
                .assertNext(todo -> assertThat(todo).isEqualToIgnoringGivenFields(TodoStubFactory.Buy_Groceries(), "id"))
                .assertNext(todo -> assertThat(todo).isEqualToIgnoringGivenFields(TodoStubFactory.Wash_Car(), "id"))
                .verifyComplete();
    }


    @Test
    public void addTodo_givenTodo_shouldSaveNewTodo() {
        //arrange
        Todo newTodo = TodoStubFactory.Buy_Groceries();

        Todo expectTodo = TodoStubFactory.Buy_Groceries();
        //act
        //assert
        webTestClient.post().uri(TodoRouter.TODO_ENDPOINT_V1).contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(newTodo), Todo.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isNotEmpty()
                .jsonPath("$.description").isEqualTo(expectTodo.getDescription())
                .jsonPath("$.details").isEqualTo(expectTodo.getDetails())
                .jsonPath("$.done").isEqualTo(expectTodo.isDone());
    }

    @Test
    public void addTodo_givenTodoWithDoneIsFalse_ShouldThrowInvalidTodoItemException() {
        //arrange
        Todo invalidTodo = TodoStubFactory.Invalid_Buy_Grocereis();

        //act
        //assert
        webTestClient.post().uri(TodoRouter.TODO_ENDPOINT_V1)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(invalidTodo), Todo.class)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.message").isEqualTo(new InvalidTodoItemException().getMessage());
    }
}
