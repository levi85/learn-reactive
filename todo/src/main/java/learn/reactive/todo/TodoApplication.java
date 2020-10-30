package learn.reactive.todo;

import learn.reactive.todo.todo.Todo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import reactor.core.publisher.EmitterProcessor;

import java.util.function.Function;
import java.util.function.Supplier;

@SpringBootApplication
public class TodoApplication {

	public static void main(String[] args) {
		SpringApplication.run(TodoApplication.class, args);
	}

	@Bean
	public EmitterProcessor<Todo> emitterProcessorBean() {
		return EmitterProcessor.create();
	}

}
