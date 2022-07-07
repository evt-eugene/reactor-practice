package com.example.demo.student.janitors.entity.callbacks;

import com.datastax.oss.driver.api.core.CqlIdentifier;
import com.example.demo.student.janitors.entity.Janitor;
import org.reactivestreams.Publisher;
import org.springframework.core.annotation.Order;
import org.springframework.data.cassandra.core.mapping.event.ReactiveBeforeConvertCallback;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@Order(101)
public class ReactiveBeforeConvertJanitorCallback implements ReactiveBeforeConvertCallback<Janitor> {

  @Override
  public Publisher<Janitor> onBeforeConvert(Janitor janitor, CqlIdentifier tableName) {
    return Mono.defer(() -> {
      var characteristic = janitor.getCharacteristic();

      if (characteristic != null && !characteristic.endsWith("...")) {
        janitor.setCharacteristic(characteristic.concat("..."));
      }

      return Mono.just(janitor);
    });
  }
}
