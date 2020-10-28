package learn.reactive.todo.todo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.function.Function;
import java.util.function.Supplier;

@Configuration
public class TodoPublisher {
//    @Bean
//    public Supplier<String> echo() {
//
//        String messageToOutput = "hello";
//
//        return () -> messageToOutput;
//    }


    @Autowired
    TodoService todoService;

    @Bean
	public Function<String, String> echo() {
		return value -> value;
	}

	@Bean
    public Supplier<String> broadcast() {
        return () -> todoService.getMessage();
    }
}
