package learn.reactive.todo.todo;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.function.Function;

public class TodoPublisher {
    @Bean
    public Function<String, String> echo() {
        return value -> value;
    }
}
