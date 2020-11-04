package learn.reactive.todo.todo;

import learn.reactive.todo.TodoApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.stream.binder.test.InputDestination;
import org.springframework.cloud.stream.binder.test.OutputDestination;
import org.springframework.cloud.stream.binder.test.TestChannelBinderConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.util.SerializationUtils;
import reactor.core.publisher.EmitterProcessor;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;

public class TodoPublisherTest {

    @Test
    public void sampleTest() {
        try (ConfigurableApplicationContext context = new SpringApplicationBuilder(
                TestChannelBinderConfiguration.getCompleteConfiguration(
                        TodoApplication.class))
                .profiles("development")
                .run("--spring.cloud.function.definition=echo;broadcast")) {
            InputDestination source = context.getBean(InputDestination.class);
            OutputDestination target = context.getBean(OutputDestination.class);
            source.send(new GenericMessage<byte[]>("helloDay".getBytes()));
            assertThat(target.receive(0,0).getPayload()).isEqualTo("helloDay".getBytes());
            assertThat(target.receive(0,1).getPayload()).isEqualTo("hello".getBytes());
        }
    }

    @Test
    public void broadcastTest() {
        try (ConfigurableApplicationContext context = new SpringApplicationBuilder(
                TestChannelBinderConfiguration.getCompleteConfiguration(
                        TodoApplication.class))
                .profiles("staging")
                .run("--spring.cloud.function.definition=broadcast")) {


            TodoService todoService = context.getBean(TodoService.class);
            EmitterProcessor<String> stringEmitterProcessor = context.getBean(EmitterProcessor.class);
            OutputDestination target = context.getBean(OutputDestination.class);

            Flux<String> result = todoService.getMessage();

            result.blockFirst();

            stringEmitterProcessor.doOnNext(message -> {
                assertThat(target.receive().getPayload()).isEqualTo("HelloWorld".getBytes());
            });
        }
    }


    @Test
    public void broadcastNewTodoTest() {
        try (ConfigurableApplicationContext context = new SpringApplicationBuilder(
                TestChannelBinderConfiguration.getCompleteConfiguration(
                        TodoApplication.class))
                .profiles("development")
                .run("--spring.cloud.function.definition=broadcastNewTodo")) {


            Todo newTodoStub = new Todo(null, "dummy desc", "dummy details", false);
            Todo expectedSavedTodo = new Todo(1, "dummy desc", "dummy details", false);

            TodoService todoService = context.getBean(TodoService.class);
            OutputDestination target = context.getBean(OutputDestination.class);

            todoService.createTodo(newTodoStub).block();

            assertThat(target.receive().getPayload()).isEqualTo(expectedSavedTodo);
        }
    }

}
