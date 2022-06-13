package com.example.demo.user.web;

import com.example.demo.user.entity.User;
import com.example.demo.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
public class UserHandler {

  private final UserService userService;

  @Autowired
  public UserHandler(UserService userService) {
    this.userService = userService;
  }

  public Mono<ServerResponse> getAllOrders(ServerRequest request) {
    var allUsers = userService.getAllUsers();

    return ServerResponse.ok()
            .contentType(MediaType.APPLICATION_JSON)
            .body(allUsers, User.class);
  }

  public Mono<ServerResponse> getOrderById(ServerRequest request) {
    var userId = Long.parseLong(request.pathVariable("userId"));

    return userService.findById(userId)
            .flatMap(user -> ServerResponse.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(user)
            )
            .switchIfEmpty(ServerResponse.notFound().build());
  }

}
