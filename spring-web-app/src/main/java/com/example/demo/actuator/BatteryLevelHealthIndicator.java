package com.example.demo.actuator;

import org.springframework.boot.actuate.health.AbstractReactiveHealthIndicator;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Random;

@Component
public class BatteryLevelHealthIndicator extends AbstractReactiveHealthIndicator {

  private final Random rnd = new Random();

  public BatteryLevelHealthIndicator() {
    super("Health check for BatteryLevelHealthIndicator failed");
  }

  @Override
  protected Mono<Health> doHealthCheck(Health.Builder builder) {
    return Mono.defer(() -> Mono.just(rnd.nextInt(100)))
        .map(level -> {
          var status = isLow(level) ? builder.up() : builder.status(new Status("Low Battery"));

          return status.withDetail("level", level).build();
        });
  }

  private boolean isLow(int level) {
    return level > 40;
  }
}
