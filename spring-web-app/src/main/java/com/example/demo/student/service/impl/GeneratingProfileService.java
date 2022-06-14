package com.example.demo.student.service.impl;

import com.example.demo.student.entity.Profile;
import com.example.demo.student.service.ProfileService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class GeneratingProfileService implements ProfileService {

  @Override
  public Mono<Profile> getByUser(String userName) {
    return Mono.just(new Profile(userName + " : paid"));
  }
}
