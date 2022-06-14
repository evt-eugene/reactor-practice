package com.example.demo;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.server.SecurityWebFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

@EnableReactiveMethodSecurity
public class SecurityConfig {

  @Bean
  public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
    return http
        .authorizeExchange(authorize -> authorize.anyExchange().permitAll())
        .formLogin(withDefaults())
        .build();
  }

  @Bean
  public ReactiveUserDetailsService userDetailsService() {
    var user = User.withDefaultPasswordEncoder()
        .username("user")
        .password("user")
        .roles("USER")
        .build();

    var admin = User.withDefaultPasswordEncoder()
        .username("admin")
        .password("admin")
        .roles("USER", "ADMIN")
        .build();

    return new MapReactiveUserDetailsService(user, admin);
  }

}
