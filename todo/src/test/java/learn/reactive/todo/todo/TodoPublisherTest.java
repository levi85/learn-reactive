package learn.reactive.todo.todo;

import learn.reactive.todo.TodoApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.stream.binder.test.InputDestination;
import org.springframework.cloud.stream.binder.test.OutputDestination;
import org.springframework.cloud.stream.binder.test.TestChannelBinderConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.messaging.support.GenericMessage;

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
                .profiles("development")
                .run("--spring.cloud.function.definition=broadcast")) {


            TodoPublisher todoPublisher = context.getBean(TodoPublisher.class);

            OutputDestination target = context.getBean(OutputDestination.class);
            assertThat(target.receive().getPayload()).isEqualTo("HelloWorld".getBytes());
        }
    }
}
