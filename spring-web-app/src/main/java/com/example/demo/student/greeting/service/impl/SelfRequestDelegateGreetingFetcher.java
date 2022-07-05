package com.example.demo.student.greeting.service.impl;

import com.example.demo.student.greeting.entity.Greeting;
import com.example.demo.student.greeting.service.GreetingFetcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;
import reactor.util.retry.RetryBackoffSpec;

import java.time.Duration;

@Component
public class SelfRequestDelegateGreetingFetcher implements GreetingFetcher {

  private static final long MAX_RETRY_ATTEMPTS = 3L;
  private static final Duration MIN_BACKOFF = Duration.ofMillis(500);

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
        .bodyToMono(Greeting.class)
        .retryWhen(retry3AttemptsWith500Ms());
  }

  private RetryBackoffSpec retry3AttemptsWith500Ms() {
    return Retry.backoff(MAX_RETRY_ATTEMPTS, MIN_BACKOFF);
  }
}
