package learn.reactive.todo.todo.exception;

public class InvalidTodoItemException extends RuntimeException {
    public InvalidTodoItemException() {
        super("new Todo item cannot be done");
    }
}
