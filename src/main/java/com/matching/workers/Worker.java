package com.matching.workers;

import com.matching.engine.MatchingEngine;
import com.matching.pojo.Asset;
import com.matching.queue.OrderQueue;

public abstract class Worker <T extends Asset> implements Runnable {

  OrderQueue<T> orderQueue;
  MatchingEngine<T> matchingEngine;

  public Worker(OrderQueue<T> orderQueue, MatchingEngine<T> matchingEngine) {
    this.orderQueue = orderQueue;
    this.matchingEngine = matchingEngine;
  }

  abstract public void run();
}
