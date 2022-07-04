package com.example.demo.student.service;

import com.example.demo.student.entity.greeting.Greeting;
import reactor.core.publisher.Mono;

public interface GreetingFetcher {

  Mono<Greeting> fetchGreeting();

}
