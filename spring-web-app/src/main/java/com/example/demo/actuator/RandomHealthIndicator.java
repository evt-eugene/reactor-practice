package com.example.demo.actuator;

import org.springframework.boot.actuate.health.AbstractReactiveHealthIndicator;
import org.springframework.boot.actuate.health.Health;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.concurrent.ThreadLocalRandom;

@Component("rnd")
public class RandomHealthIndicator extends AbstractReactiveHealthIndicator {

  public RandomHealthIndicator() {
    super("Health check for RandomHealthIndicator failed");
  }

  @Override
  public Mono<Health> doHealthCheck(Health.Builder builder) {
    return Mono.defer(() -> Mono.just(ThreadLocalRandom.current().nextDouble()))
        .map(chance -> {
          var status = (chance > 0.9) ? Health.up() : Health.down();
          return status.build();
        });
  }
}
