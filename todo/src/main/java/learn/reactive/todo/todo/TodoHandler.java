package learn.reactive.todo.todo;

import learn.reactive.todo.todo.exception.InvalidTodoItemException;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.http.codec.multipart.Part;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.*;
import java.nio.file.Files;
import java.util.Base64;
import java.util.List;
import java.util.Map;

@Slf4j
@AllArgsConstructor
@Component
public class TodoHandler {

    private final TodoRepository todoRepository;

    private final TodoService todoService;

    public Mono<ServerResponse> getAllItems(ServerRequest serverRequest) {

        return  ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(todoRepository.findAll(), Todo.class);


    }

    public Mono<ServerResponse> newTodo(ServerRequest serverRequest) {


        Mono<Todo> newTodo = serverRequest.bodyToMono(Todo.class);

        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(newTodo
                        .map(todo -> {
                            if (todo.isDone() == true) {
                                throw new InvalidTodoItemException();
                            }
                            return todo;
                        })
                        .flatMap(todo -> todoRepository.save(todo)), Todo.class);
    }

    public Mono<ServerResponse> updateTodo(ServerRequest serverRequest) {

        Mono<Todo> reqTodo = serverRequest.bodyToMono(Todo.class);


        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(reqTodo.flatMap(todo -> todoService.updateTodo(todo)), Todo.class);
    }

    public Mono<ServerResponse> createMessage(ServerRequest serverRequest) {

        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(todoService.getMessage(), String.class);
    }

    public Mono<ServerResponse> uploadFile(ServerRequest serverRequest)  {

        log.info("OMG");

//        File testFile = new File("TESTME.txt");
//        try {
//            testFile.createNewFile();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        Mono<MultiValueMap<String, Part>> multiValueMapMono = serverRequest.multipartData();

        return multiValueMapMono.flatMap(parts -> {
            parts.forEach((key, fileparts) -> {
                log.info("processing");

                log.info("Key is: " + key );

                fileparts.forEach(part -> {
                    FilePart filePart = (FilePart) part;
                    String fileName = filePart.filename();

                    log.info("FILE NAME RECEIVED: " + fileName);

                    File file = new File(fileName);

                    filePart.transferTo(file);

                    byte[] fileContent = new byte[0];

                    try {
                        fileContent = Files.readAllBytes(file.toPath());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    String based64FileContent = Base64.getEncoder().encodeToString(fileContent);
                    String xmlFileToSend = createXmlFileContentWithFile(fileName, based64FileContent);

                    FileWriter fileWriter = null;
                    PrintWriter printWriter = null;

                    try {
                        fileWriter = new FileWriter("pr.xml");
                        printWriter = new PrintWriter(fileWriter);
                        printWriter.print(xmlFileToSend);

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }finally {
                        printWriter.close();
                    }
                });

            });




            return ServerResponse.ok().body(BodyInserters.fromValue("OK"));
        });
    }

    private void copyFile(String fileName, String based64FileContent) {
        byte[] decodedFileContent = Base64.getDecoder().decode(based64FileContent);

        FileOutputStream fos = null;

        try {
            fos = new FileOutputStream("COPY_" + fileName);
            fos.write(decodedFileContent);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private String createXmlFileContentWithFile(String filename, String based64Content) {

        final String NEW_LINE = System.getProperty("line.separator");

        StringBuilder sb = new StringBuilder();

        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        sb.append(NEW_LINE);
        sb.append("<shoppingcart>");
        sb.append(NEW_LINE);
        sb.append("<header>");
        sb.append(NEW_LINE);
        sb.append("</header>");
        sb.append(NEW_LINE);
        sb.append("<items>");
        sb.append(NEW_LINE);
        sb.append("<item>");
        sb.append(NEW_LINE);
        sb.append("<desc>HP Notebook</desc>");
        sb.append(NEW_LINE);
        sb.append("<price>1200</price>");
        sb.append(NEW_LINE);
        sb.append("<attachments>");
        sb.append(NEW_LINE);
        sb.append("<filename>");
        sb.append(filename);
        sb.append("</filename>");
        sb.append(NEW_LINE);
        sb.append("<filedata>");
        sb.append(based64Content);
        sb.append("</filedata>");
        sb.append(NEW_LINE);
        sb.append("</attachments>");
        sb.append(NEW_LINE);
        sb.append("</item>");
        sb.append(NEW_LINE);
        sb.append("<item>");
        sb.append(NEW_LINE);
        sb.append("<desc>Dell Notebook</desc>");
        sb.append(NEW_LINE);
        sb.append("<price>1500</price>");
        sb.append(NEW_LINE);
        sb.append("<attachments>");
        sb.append(NEW_LINE);
        sb.append("<filename>");
        sb.append(filename);
        sb.append("</filename>");
        sb.append(NEW_LINE);
        sb.append("<filedata>");
        sb.append(based64Content);
        sb.append("</filedata>");
        sb.append(NEW_LINE);
        sb.append("</attachments>");
        sb.append(NEW_LINE);
        sb.append("</item>");
        sb.append(NEW_LINE);
        sb.append("</items>");
        sb.append(NEW_LINE);
        sb.append("</shoppingcart>");
        return sb.toString();
    }
}
