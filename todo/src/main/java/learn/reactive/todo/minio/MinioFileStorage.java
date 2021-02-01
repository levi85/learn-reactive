package learn.reactive.todo.minio;

import io.minio.*;
import io.minio.errors.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;


@Slf4j
public class MinioFileStorage implements FileStorage {

    MinioClient minioClient =
            MinioClient.builder()
                    .endpoint("http://localhost:9000")
                    .credentials("minioadmin", "minioadmin")
                    .build();

    @Override
    public Mono<String> storeFile(AttachmentDto attachmentDto) {

        try {
            boolean found =
                    minioClient.bucketExists(BucketExistsArgs.builder().bucket("ncitem" + attachmentDto.getNcItemId()).build());
            if (!found) {
                // Make a new bucket called 'asiatrip'.
                minioClient.makeBucket(MakeBucketArgs.builder().bucket("ncitem" + attachmentDto.getNcItemId()).build());
            } else {
                log.info("Bucket 'asiatrip' already exists.");
            }

            minioClient.putObject(PutObjectArgs.builder().bucket("ncitem" + attachmentDto.getNcItemId())
                    .object(attachmentDto.getFileName()).stream(new ByteArrayInputStream(attachmentDto.getData()), attachmentDto.getData().length, -1).build());

            return Mono.just("Success");

        } catch (Exception minioException) {
            log.error(minioException.getMessage());
            return Mono.error(minioException);
        }
    }

    @Override
    public Flux<DataBuffer> retrieveFile(AttachmentDto attachmentDto) {
        try {
            InputStream stream = minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket("ncitem"+attachmentDto.getNcItemId())
                            .object(attachmentDto.getFileName())
                            .build());
            // Read data from stream

            DataBuffer objectContent = new DefaultDataBufferFactory().wrap(stream.readAllBytes());

            return Flux.just(objectContent);

        } catch (Exception minioException) {
            return Flux.error(minioException);
        }
    }

    @Override
    public Mono<Void> deleteFile(AttachmentDto attachmentDto) {
        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder().bucket("ncitem"+attachmentDto.getNcItemId()).object(attachmentDto.getFileName()).build());
        } catch (Exception minioException) {
            return Mono.error(minioException);
        }

        return Mono.empty();
    }
}
