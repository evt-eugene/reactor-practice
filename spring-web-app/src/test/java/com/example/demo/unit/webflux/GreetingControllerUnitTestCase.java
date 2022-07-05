package com.example.demo.unit.webflux;

import com.example.demo.student.greeting.entity.Greeting;
import com.example.demo.student.greeting.service.GreetingService;
import com.example.demo.student.greeting.web.GreetingController;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;


@WebFluxTest(GreetingController.class)
@Import(GreetingControllerUnitTestCase.MeterRegistryConfig.class)
public class GreetingControllerUnitTestCase {

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

  @TestConfiguration
  static class MeterRegistryConfig {

    @Bean
    public MeterRegistry registry() {
      return new SimpleMeterRegistry();
    }
  }
}
