package learn.reactive.todo.todo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Todo implements Serializable {
    @Id
    private Integer id;
    private String description;
    private String details;
    private boolean done;

}
