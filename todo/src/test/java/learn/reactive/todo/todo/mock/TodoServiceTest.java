package learn.reactive.todo.todo.mock;

import learn.reactive.todo.todo.Todo;
import learn.reactive.todo.todo.TodoRepository;
import learn.reactive.todo.todo.TodoService;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TodoServiceTest {

    @Mock
    TodoRepository todoRepository;

    //sut
    @InjectMocks
    TodoService todoService;


    @Test
    public void updateTodo_givenExistingTodoWithDoneIsTrue_shouldUpdateInRepo() {

        //arrange
        Todo stubTodo = TodoStubFactory.Buy_Groceries();
        stubTodo.setId(1);

        lenient().when(todoRepository.findById(anyInt())).thenReturn(Mono.just(stubTodo));

        Todo reqUpdateTodo = TodoStubFactory.Buy_Groceries();
        reqUpdateTodo.setId(1);
        reqUpdateTodo.setDone(true);

        //act
        todoService.updateTodo(reqUpdateTodo);


        //assert
        verify(todoRepository,times(1) ).save(any(Todo.class));
    }
}
