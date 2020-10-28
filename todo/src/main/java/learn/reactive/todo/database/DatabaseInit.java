package learn.reactive.todo.database;

import io.r2dbc.spi.ConnectionFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.r2dbc.connectionfactory.init.ConnectionFactoryInitializer;
import org.springframework.data.r2dbc.connectionfactory.init.ResourceDatabasePopulator;

//@Configuration
public class DatabaseInit extends ConnectionFactoryInitializer {
    public DatabaseInit(ConnectionFactory connectionFactory) {
        this.setConnectionFactory(connectionFactory);
        ResourceDatabasePopulator populator = new ResourceDatabasePopulator(new ClassPathResource("schema.sql"));
        this.setDatabasePopulator(populator);
    }
}
