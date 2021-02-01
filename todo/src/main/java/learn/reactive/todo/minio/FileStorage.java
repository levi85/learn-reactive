package learn.reactive.todo.minio;

import org.springframework.core.io.buffer.DataBuffer;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface FileStorage {

    Mono<String> storeFile(AttachmentDto attachmentDto);

    Flux<DataBuffer> retrieveFile(AttachmentDto attachmentDto);

    Mono<Void> deleteFile(AttachmentDto attachmentDto);
}