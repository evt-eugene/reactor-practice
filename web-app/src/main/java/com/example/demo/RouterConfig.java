package com.example.demo;

import com.example.demo.user.web.UserHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class RouterConfig {

  @Bean
  public RouterFunction<ServerResponse> routes(UserHandler handler) {
    return route(GET("/users").and(accept(MediaType.APPLICATION_JSON)), handler::getAllUsers)
        .andRoute(GET("/users/stream").and(accept(MediaType.TEXT_EVENT_STREAM)), handler::streamAllUsers)
        .andRoute(GET("/users/{userId}"), handler::getUserById);
  }
}
