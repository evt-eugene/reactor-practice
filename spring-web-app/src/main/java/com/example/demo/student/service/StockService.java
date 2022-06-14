package com.example.demo.student.service;

import com.example.demo.student.entity.StockItem;
import reactor.core.publisher.Flux;

public interface StockService {

  Flux<StockItem> stocks();

}
