package learn.reactive.todo.minio;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AttachmentDto {

    private long ncItemId;
    private long attachmentId;
    private String fileName;
    private byte[] data;
}
