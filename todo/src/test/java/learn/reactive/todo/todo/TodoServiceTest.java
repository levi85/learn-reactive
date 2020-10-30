package learn.reactive.todo.todo;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.EmitterProcessor;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class TodoServiceTest {

    @Mock
    private TodoRepository todoRepositoryMock;

    @Mock
    private EmitterProcessor<Todo> emitterProcessor;

    @InjectMocks
    private TodoService todoService;

    @Test
    public void createTodo_givenNewTodo_shouldCreateItem() {
        //arrange
        Todo newItem = new Todo(null, "Dummy Description", "Dummy details", false);
        Todo savedItemStub = new Todo(1, "Dummy Description", "Dummy details", false);

        when(todoRepositoryMock.save(any(Todo.class))).thenReturn(Mono.just(savedItemStub));

        ArgumentCaptor<Todo> todoArgumentCaptor = ArgumentCaptor.forClass(Todo.class);

        //act
        Mono<Todo> result = todoService.createTodo(newItem);

        //assert
        verify(todoRepositoryMock, times(1)).save(todoArgumentCaptor.capture());
        assertThat(todoArgumentCaptor.getValue()).isEqualToComparingFieldByField(newItem);

        StepVerifier.create(result.log())
                .expectSubscription()
                .expectNextCount(1L)
                .verifyComplete();
    }

    @Test
    public void createTodo_givenNewTodo_shouldPublishEvents() {
        //arrange
        Todo newItem = new Todo(null, "Dummy Description", "Dummy details", false);
        Todo savedItemStub = new Todo(1, "Dummy Description", "Dummy details", false);
        when(todoRepositoryMock.save(any(Todo.class))).thenReturn(Mono.just(savedItemStub));

        ArgumentCaptor<Todo> todoArgumentCaptor = ArgumentCaptor.forClass(Todo.class);

        //act
        Mono<Todo> result = todoService.createTodo(newItem);


        //assert
        result.block();
        verify(emitterProcessor, times(1)).onNext(todoArgumentCaptor.capture());
    }
}
