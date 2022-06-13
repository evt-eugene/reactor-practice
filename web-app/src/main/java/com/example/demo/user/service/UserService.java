package com.example.demo.user.service;

import com.example.demo.user.entity.User;
import com.example.demo.user.persistance.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class UserService {

  private final UserRepository userRepository;

  @Autowired
  public UserService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  public Flux<User> getAllUsers() {
    return userRepository.findAll();
  }

  public Mono<User> findById(Long userId) {
    return userRepository.findById(userId);
  }
}
