package com.example.demo.student.stocks.entity;

public class StockItem {

  private final Long id;
  private final Currency currency;

  public StockItem(Long id, Currency currency) {
    this.id = id;
    this.currency = currency;
  }

  public Long getId() {
    return id;
  }

  public Currency getCurrency() {
    return currency;
  }
}
