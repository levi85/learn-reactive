package learn.reactive.todo.todo.mock;

import learn.reactive.todo.todo.Todo;

public class TodoStubFactory {

    public static Todo Buy_Groceries() {
        return new Todo(null, "Buy Groceries", "Apple, orange", false);
    }

    public static Todo Wash_Car() {
        return new Todo(null, "Wash Car", "Soap, Wash, vacuum", false);
    }
}
