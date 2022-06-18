package com.example.demo.integration.webflux;

import com.example.demo.student.entity.Greeting;
import com.example.demo.student.service.GreetingService;
import com.example.demo.student.web.GreetingController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;


@WebFluxTest(GreetingController.class)
public class GreetingControllerIntegrationTests {

  @Autowired
  private WebTestClient webTestClient;

  @MockBean
  private GreetingService service;

  @Test
  @WithMockUser
  public void testGetGreeting() {
    when(service.getGreeting()).thenReturn(createGreeting("Hello, Spring!"));

    webTestClient
        .get().uri("/greeting/get")
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus().isOk()
        .expectBody(Greeting.class).value(greeting -> {
          assertThat(greeting.getMessage()).isEqualTo("Hello, Spring!");
        });

    verify(service).getGreeting();
    verify(service, never()).fetchGreeting();
  }

  @Test
  @WithMockUser
  public void testFetchGreeting() {
    when(service.fetchGreeting()).thenReturn(createGreeting("Hello, Spring!"));

    webTestClient
        .get().uri("/greeting/fetch")
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus().isOk()
        .expectBody(Greeting.class).value(greeting -> {
          assertThat(greeting.getMessage()).isEqualTo("Hello, Spring!");
        });

    verify(service).fetchGreeting();
    verify(service, never()).getGreeting();
  }

  private static Mono<Greeting> createGreeting(String message) {
    return Mono.just(new Greeting(message));
  }
}
