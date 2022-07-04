package com.example.demo.student.service;

import com.example.demo.student.entity.profile.Profile;
import reactor.core.publisher.Mono;

public interface ProfileService {

  Mono<Profile> getByUser(String userName);

}
