package com.example.demo.student.greeting.service.impl;

import com.example.demo.student.greeting.entity.Greeting;
import com.example.demo.student.greeting.service.GreetingGetter;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Service
public class InMemoryGreetingGetter implements GreetingGetter {

  private static final Greeting DEFAULT_GREETING = new Greeting("Hello, Spring!");

  @Override
  public Mono<Greeting> get() {
    return Mono.just(DEFAULT_GREETING).delayElement(Duration.ofSeconds(2));
  }
}
