package com.example.demo.student.web;

import com.example.demo.student.entity.stocks.StockItem;
import com.example.demo.student.service.StockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import reactor.core.publisher.Flux;

@Controller
@RequestMapping("/stocks")
public class StocksController {

  private final StockService service;

  @Autowired
  public StocksController(StockService service) {
    this.service = service;
  }

  @GetMapping("/view")
  public String view() {
    return "stocks-view.html";
  }

  @GetMapping(value = "/sse/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
  @ResponseBody
  public Flux<ServerSentEvent<StockItem>> stocks() {
    return service.stocks()
        .map(item -> ServerSentEvent.<StockItem>builder()
            .id(String.valueOf(item.getId()))
            .event("StockItem")
            .data(item)
            .build()
        );
  }
}
