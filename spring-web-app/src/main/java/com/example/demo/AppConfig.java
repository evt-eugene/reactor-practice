package com.example.demo;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.cassandra.config.EnableReactiveCassandraAuditing;
import org.springframework.data.domain.ReactiveAuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.userdetails.User;
import reactor.core.publisher.Mono;

@Configuration
@EnableReactiveCassandraAuditing
public class AppConfig {

  @Bean
  public ReactiveAuditorAware<String> reactiveAuditorAware() {
    return new UsernameAuditorAware();
  }

  private static final class UsernameAuditorAware implements ReactiveAuditorAware<String> {

    @Override
    public Mono<String> getCurrentAuditor() {
      return ReactiveSecurityContextHolder.getContext()
          .map(SecurityContext::getAuthentication)
          .filter(Authentication::isAuthenticated)
          .map(Authentication::getPrincipal)
          .map(User.class::cast)
          .map(User::getUsername);
    }
  }
}
