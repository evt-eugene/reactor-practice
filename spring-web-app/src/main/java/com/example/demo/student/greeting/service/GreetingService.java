package com.example.demo.student.greeting.service;

import com.example.demo.student.greeting.entity.Greeting;
import reactor.core.publisher.Mono;

public interface GreetingService {

  Mono<Greeting> getGreeting();

  Mono<Greeting> fetchGreeting();

}
