package com.example.demo.user.persistance;

import com.example.demo.user.entity.User;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UserRepository {

  Flux<User> findAll();

  Mono<User> findById(Long userId);

}
