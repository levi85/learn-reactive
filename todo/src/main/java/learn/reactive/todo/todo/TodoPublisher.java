package learn.reactive.todo.todo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;
import reactor.core.publisher.EmitterProcessor;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.function.Consumer;
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

//    @Autowired
//    EmitterProcessor<Todo> emitterProcessor;

    @Autowired
    EmitterProcessor<String> stringEmitterProcessor;

    @Bean
	public Function<String, String> echo() {
		return value -> value;
	}

    @Bean
    public Consumer<String> receiver() {
        return message -> System.out.println(message);
    }

    @Bean
    public Supplier<Flux<String>> broadcast() {
        return () -> {

//            return stringEmitterProcessor;

            return Flux.interval(Duration.ofSeconds(1)).map(value -> "Hello " + value);
        };
    }
}
