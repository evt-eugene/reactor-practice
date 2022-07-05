package com.example.demo.student.stocks.service.impl;

import com.example.demo.student.stocks.entity.Currency;
import com.example.demo.student.stocks.entity.StockItem;
import com.example.demo.student.stocks.service.StockService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.time.Duration;

@Service
public class GeneratingStocksService implements StockService {

  @Override
  public Flux<StockItem> stocks() {
    return Flux.<StockItem, Long>generate(
            () -> 0L,
            (state, synk) -> {
              var currency = isPrime(state) ? Currency.USD : Currency.UAH;

              synk.next(new StockItem(state, currency));

              return state + 1;
            })
        .delayElements(Duration.ofSeconds(1));
  }

  private static boolean isPrime(Long val) {
    return val % 2 == 0;
  }
}
