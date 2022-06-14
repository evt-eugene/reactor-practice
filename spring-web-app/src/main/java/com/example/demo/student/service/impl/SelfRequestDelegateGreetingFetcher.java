package com.example.demo.student.service.impl;

import com.example.demo.student.entity.Greeting;
import com.example.demo.student.service.GreetingFetcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class SelfRequestDelegateGreetingFetcher implements GreetingFetcher {

  private final WebClient client;

  @Autowired
  public SelfRequestDelegateGreetingFetcher(WebClient.Builder builder) {
    this.client = builder
        .baseUrl("http://localhost:8080")
        .build();
  }

  @Override
  public Mono<Greeting> fetchGreeting() {
    return this.client
        .get()
        .uri("/greeting/get")
        .accept(MediaType.APPLICATION_JSON)
        .retrieve()
        .bodyToMono(Greeting.class);
  }
}
