package com.example.demo;

import com.example.demo.student.entity.Greeting;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class GreetingControllerTest {

  @Autowired
  private WebTestClient webTestClient;

  @Test
  public void testHello() {
    webTestClient
        .get().uri("/greeting/get")
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus().isOk()
        .expectBody(Greeting.class).value(greeting -> {
          assertThat(greeting.getMessage()).isEqualTo("Hello, Spring!");
        });
  }

}
