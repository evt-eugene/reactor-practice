package com.example.demo.student.profile.service;

import com.example.demo.student.profile.entity.Profile;
import reactor.core.publisher.Mono;

public interface ProfileService {

  Mono<Profile> getByUser(String userName);

}
