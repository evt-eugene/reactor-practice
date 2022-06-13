package com.example.demo.user.web;

import com.example.demo.user.entity.User;
import com.example.demo.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.stream.Stream;

@Component
public class UserHandler {

  private final UserService userService;

  @Autowired
  public UserHandler(UserService userService) {
    this.userService = userService;
  }

  public Mono<ServerResponse> getAllUsers(ServerRequest request) {
    var allUsers = userService.getAllUsers();

    return ServerResponse.ok()
        .contentType(MediaType.APPLICATION_JSON)
        .body(allUsers, User.class);
  }

  public Mono<ServerResponse> getUserById(ServerRequest request) {
    var userId = Long.parseLong(request.pathVariable("userId"));

    return userService.findById(userId)
        .flatMap(user -> ServerResponse.ok()
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(user)
        )
        .switchIfEmpty(ServerResponse.notFound().build());
  }

  public Mono<ServerResponse> streamAllUsers(ServerRequest request) {
    var events = userService.getAllUsers()
        .flatMap(user ->
                     Flux.zip(
                             Flux.interval(Duration.ofSeconds(2)),
                             Flux.fromStream(Stream.generate(() -> user))
                         )
                         .map(tuple -> {
                           var id = tuple.getT1();
                           var u = tuple.getT2();

                           return ServerSentEvent.builder()
                               .id(id + "_" + u.getId())
                               .data(u)
                               .comment("Server Side Event for User " + u.getName())
                               .build();
                         })
        );

    return ServerResponse.ok()
        .contentType(MediaType.TEXT_EVENT_STREAM)
        .body(events, User.class);

  }
}
