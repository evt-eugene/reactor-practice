package com.example.demo.user.service;

import com.example.demo.user.entity.User;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UserService {

  Flux<User> getAllUsers();

  Mono<User> findById(Long userId);

}
