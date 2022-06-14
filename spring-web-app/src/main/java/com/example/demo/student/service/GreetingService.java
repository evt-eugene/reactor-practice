package com.example.demo.student.service;

import com.example.demo.student.entity.Greeting;
import reactor.core.publisher.Mono;

public interface GreetingService {

  Mono<Greeting> getGreeting();

  Mono<Greeting> fetchGreeting();

}
