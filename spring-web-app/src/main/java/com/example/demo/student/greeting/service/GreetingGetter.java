package com.example.demo.student.greeting.service;

import com.example.demo.student.greeting.entity.Greeting;
import reactor.core.publisher.Mono;

public interface GreetingGetter {

  Mono<Greeting> get();

}
