package standalone;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.Exceptions;
import reactor.core.publisher.Mono;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.TEXT_PLAIN;

public class Handler {

  public static Mono<ServerResponse> hello(ServerRequest req) {
    return ServerResponse.ok()
        .contentType(TEXT_PLAIN)
        .body(BodyInserters.fromValue("Hello, Spring WebFlux Example!"));
  }

  public static Mono<ServerResponse> getUserById(ServerRequest req) {
    var userId = Long.valueOf(req.pathVariable("userId"));
    var greeting = new Greeting(userId, "Hi, dear user with id = " + userId);
    var json = toJson(greeting);

    return ServerResponse.ok()
        .contentType(APPLICATION_JSON)
        .body(BodyInserters.fromValue(json));
  }

  private static String toJson(Object o) {
    try {
      return new ObjectMapper().writeValueAsString(o);
    } catch (JsonProcessingException e) {
      throw Exceptions.propagate(e);
    }
  }

}
