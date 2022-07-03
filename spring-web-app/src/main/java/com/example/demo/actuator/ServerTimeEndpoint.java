package com.example.demo.actuator;

import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.Map;

@Component
@Endpoint(id = "servertime")
public class ServerTimeEndpoint {

  @ReadOperation
  public Mono<Map<String, Object>> reportServerTime() {
    return Mono.just(Map.of("serverTime", Instant.now().toString()));
  }
}
