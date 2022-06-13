package com.example.demo.user.persistance;

import com.example.demo.user.entity.User;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.StreamSupport;

@Repository
public class UserRepository {

  private final Iterable<User> ALL_USERS = List.of(
      new User(1L, "John Doe"),
      new User(2L, "Jack Sparrow")
  );

  public Flux<User> findAll() {
    return Flux.fromIterable(ALL_USERS);
  }

  public Mono<User> findById(Long userId) {
    return StreamSupport.stream(ALL_USERS.spliterator(), false)
        .filter(u -> u.getId().equals(userId))
        .findFirst()
        .map(Mono::just)
        .orElse(Mono.empty());
  }
}
