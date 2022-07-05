package com.example.demo.student.stocks.service;

import com.example.demo.student.stocks.entity.StockItem;
import reactor.core.publisher.Flux;

public interface StockService {

  Flux<StockItem> stocks();

}
