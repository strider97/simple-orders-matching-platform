package com.matching.workers;

import com.matching.engine.MatchingEngine;
import com.matching.pojo.Order;
import com.matching.pojo.Stock;
import com.matching.queue.OrderQueue;

public class StockWorker extends Worker<Stock> {

  public StockWorker(OrderQueue<Stock> orderQueue, MatchingEngine<Stock> matchingEngine) {
    super(orderQueue, matchingEngine);
  }

  @Override
  public void run() {
    while(true) {
      Order<Stock> stockOrder = orderQueue.takeOrder();
      matchingEngine.processOrder(stockOrder);
    }
  }
}
