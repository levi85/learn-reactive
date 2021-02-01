package learn.reactive.todo.todo;

import io.minio.*;
import io.minio.errors.*;
import io.minio.messages.Item;
import learn.reactive.todo.minio.AttachmentDto;
import learn.reactive.todo.minio.MinioFileStorage;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
public class MinioTest {

    private MinioFileStorage minioFileStorage = new MinioFileStorage();

    MinioClient minioClient;

    @BeforeEach
    public void setup() {
        minioClient = MinioClient.builder()
                .endpoint("http://localhost:9000")
                .credentials("minioadmin", "minioadmin")
                .build();
    }

    @Test
    public void storefile_givenFileDto_shouldCreateBucket() throws IOException, InvalidKeyException, InvalidResponseException, InsufficientDataException, NoSuchAlgorithmException, ServerException, InternalException, XmlParserException, ErrorResponseException {
        //arrange
        deleteBucket("ncitem1");
        AttachmentDto attachmentDtoReq = new AttachmentDto(1, 0, "samplefile.txt", "Hellworld".getBytes());

        //act
        String result = minioFileStorage.storeFile(attachmentDtoReq).block();

        //assert
        boolean found = minioClient.bucketExists(BucketExistsArgs.builder().bucket("ncitem1").build());
        assertThat(found).isTrue();
    }

    @Test
    public void storefile_givenFileDto_shouldStoreFile() throws IOException, InvalidKeyException, InvalidResponseException, InsufficientDataException, NoSuchAlgorithmException, ServerException, InternalException, XmlParserException, ErrorResponseException {
        //arrange
        deleteBucket("ncitem1");
        AttachmentDto attachmentDtoReq = new AttachmentDto(1, 0, "samplefile.txt", "Hellworld".getBytes());

        //act
        String result = minioFileStorage.storeFile(attachmentDtoReq).block();

        //assert
        InputStream stream = minioClient.getObject(
                GetObjectArgs.builder().bucket("ncitem" + 1).object("samplefile.txt").build()
        );


        assertThat(stream.readAllBytes()).isEqualTo("Hellworld".getBytes());
    }

    @Test
    public void retreiveFile_givenAttachmentDto_shouldReturnDataBuffer() throws IOException, InvalidResponseException, InvalidKeyException, NoSuchAlgorithmException, ServerException, ErrorResponseException, XmlParserException, InsufficientDataException, InternalException {
        //arrange
        this.deleteBucket("ncitem1");
        AttachmentDto attachmentDtoReq = new AttachmentDto(1, 2, "testfile.txt", null);

        minioClient.makeBucket(MakeBucketArgs.builder().bucket("ncitem" + attachmentDtoReq.getNcItemId()).build());

        minioClient.putObject(PutObjectArgs.builder().bucket("ncitem" + attachmentDtoReq.getNcItemId())
                .object(attachmentDtoReq.getFileName()).stream(new ByteArrayInputStream("HelloWorld".getBytes()), "HelloWorld".getBytes().length, -1).build());

        //act
        Flux<DataBuffer> result = minioFileStorage.retrieveFile(attachmentDtoReq);

        //assert
        StepVerifier.create(result.log())
                .expectSubscription()
                .expectNext(new DefaultDataBufferFactory().wrap("HelloWorld".getBytes()))
                .verifyComplete();
    }

    @Test
    public void deleteFile_givenAttachmentDto_shouldRemoveObject() throws IOException, InvalidResponseException, InvalidKeyException, NoSuchAlgorithmException, ServerException, ErrorResponseException, XmlParserException, InsufficientDataException, InternalException {
        //arrange
        this.deleteBucket("ncitem1");
        AttachmentDto attachmentDtoReq = new AttachmentDto(1, 2, "testfile.txt", null);

        minioClient.makeBucket(MakeBucketArgs.builder().bucket("ncitem" + attachmentDtoReq.getNcItemId()).build());

        minioClient.putObject(PutObjectArgs.builder().bucket("ncitem" + attachmentDtoReq.getNcItemId())
                .object(attachmentDtoReq.getFileName()).stream(new ByteArrayInputStream("HelloWorld".getBytes()), "HelloWorld".getBytes().length, -1).build());

        //act
        minioFileStorage.deleteFile(attachmentDtoReq).block();

        //assert
        Iterable<Result<Item>> results = minioClient.listObjects(
                ListObjectsArgs.builder().bucket("ncitem1").build());

        List<Item> itemList = new ArrayList<>();
        for (Result<Item> itemResult : results) {
            itemList.add(itemResult.get());
        }

        assertThat(itemList).extracting("objectName").doesNotContain("testfile.txt");

    }

    private void deleteBucket(String bucketname) throws IOException, InvalidKeyException, InvalidResponseException, InsufficientDataException, NoSuchAlgorithmException, ServerException, InternalException, XmlParserException, ErrorResponseException {

        boolean found =
                minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketname).build());
        if (found) {
            Iterable<Result<Item>> results = minioClient.listObjects(
                    ListObjectsArgs.builder().bucket(bucketname).build());
            for (Result<Item> itemResult : results) {
                System.out.println(itemResult.get().objectName());

                minioClient.removeObject(
                        RemoveObjectArgs.builder().bucket(bucketname).object(itemResult.get().objectName()).build());
            }

            minioClient.removeBucket(RemoveBucketArgs.builder().bucket(bucketname).build());

        } else {
            System.out.println("No such bucket exist");
        }
    }


}
