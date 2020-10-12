package learn.reactive.todo.todo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Todo {
    @Id
    private Integer id;
    private String description;
    private String details;
    private boolean done;

}
