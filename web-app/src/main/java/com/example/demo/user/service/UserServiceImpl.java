package com.example.demo.user.service;

import com.example.demo.user.entity.User;
import com.example.demo.user.persistance.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;

  @Autowired
  public UserServiceImpl(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  public Flux<User> getAllUsers() {
    return userRepository.findAll();
  }

  @Override
  public Mono<User> findById(Long userId) {
    return userRepository.findById(userId);
  }
}
