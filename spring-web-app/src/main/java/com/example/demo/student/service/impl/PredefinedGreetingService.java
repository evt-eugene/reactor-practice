package com.example.demo.student.service.impl;

import com.example.demo.student.entity.Greeting;
import com.example.demo.student.service.GreetingFetcher;
import com.example.demo.student.service.GreetingGetter;
import com.example.demo.student.service.GreetingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class PredefinedGreetingService implements GreetingService {

  private final GreetingGetter greetingGetter;
  private final GreetingFetcher greetingFetcher;

  @Autowired
  public PredefinedGreetingService(
      GreetingGetter greetingGetter,
      GreetingFetcher greetingFetcher) {
    this.greetingGetter = greetingGetter;
    this.greetingFetcher = greetingFetcher;
  }

  @Override
  public Mono<Greeting> getGreeting() {
    return greetingGetter.get();
  }

  @Override
  public Mono<Greeting> fetchGreeting() {
    return greetingFetcher.fetchGreeting();
  }
}
