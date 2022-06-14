package com.example.demo.student.web;

import com.example.demo.student.entity.Profile;
import com.example.demo.student.service.ProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/profiles")
public class SecuredProfileController {

  private final ProfileService profileService;

  @Autowired
  public SecuredProfileController(ProfileService profileService) {
    this.profileService = profileService;
  }

  @GetMapping
  @PreAuthorize("hasRole('ADMIN')")
  public Mono<Profile> getProfile() {
    return ReactiveSecurityContextHolder.getContext()
        .map(SecurityContext::getAuthentication)
        .flatMap(auth -> profileService.getByUser(auth.getName()));
  }
}