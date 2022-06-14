package com.example.demo.student.service;

import com.example.demo.student.entity.Greeting;
import reactor.core.publisher.Mono;

public interface GreetingFetcher {

  Mono<Greeting> fetchGreeting();

}
