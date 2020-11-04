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
import org.springframework.messaging.converter.SimpleMessageConverter;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.util.SerializationUtils;
import reactor.core.publisher.EmitterProcessor;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;

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
    public void broadcastTest() throws InterruptedException {
        try (ConfigurableApplicationContext context = new SpringApplicationBuilder(
                TestChannelBinderConfiguration.getCompleteConfiguration(
                        TodoApplication.class))
                .profiles("staging")
                .run()) {


            TodoService todoService = context.getBean(TodoService.class);
            EmitterProcessor<String> stringEmitterProcessor = context.getBean(EmitterProcessor.class);
            OutputDestination target = context.getBean(OutputDestination.class);

            Flux<String> result = todoService.getMessage();
            Thread.sleep(2000);
            assertThat(target.receive(0,0).getPayload()).isEqualTo("HelloWorld".getBytes());
        }
    }


    @Test
    public void broadcastTodoTest() throws InterruptedException, IOException, ClassNotFoundException {
        try (ConfigurableApplicationContext context = new SpringApplicationBuilder(
                TestChannelBinderConfiguration.getCompleteConfiguration(
                        TodoApplication.class))
                .profiles("development")
                .run()) {


            TodoService todoService = context.getBean(TodoService.class);
            OutputDestination target = context.getBean(OutputDestination.class);

            todoService.syncTodo().subscribe();

            todoService.todoEmitterProcessor.doOnNext(todo -> {
                System.out.println("Todo idX: " + todo.getId());
                System.out.println("Strange....");
            }).subscribe();

            Thread.sleep(3000);

            assertThat(target.receive(0, 1).getPayload()).isNotEmpty();
        }
    }

}
