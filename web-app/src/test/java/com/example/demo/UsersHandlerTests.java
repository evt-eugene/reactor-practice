package com.example.demo;

import com.example.demo.user.web.UserHandler;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@SpringBootTest
public class UsersHandlerTests {

  @Autowired
  private UserHandler handler;

  @Test
  void testAllUsers() {
    WebTestClient.bindToRouterFunction(
            route(GET("/users"), handler::getAllUsers)
        )
        .build()
        .get()
        .uri("/users")
        .exchange()
        .expectAll(
            rs -> rs.expectStatus().isOk(),
            rs -> rs.expectBody().json("[{'id': 1, 'name': 'John Doe'}, {'id': 2, 'name': 'Jack Sparrow'}]")
        );
  }
}
