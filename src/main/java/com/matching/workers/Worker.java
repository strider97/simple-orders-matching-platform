package com.matching.workers;

import com.matching.engine.MatchingEngine;
import com.matching.pojo.Asset;
import com.matching.queue.OrderQueue;

public abstract class Worker  implements Runnable {

  OrderQueue orderQueue;
  MatchingEngine matchingEngine;

  public Worker(OrderQueue orderQueue, MatchingEngine matchingEngine) {
    this.orderQueue = orderQueue;
    this.matchingEngine = matchingEngine;
  }

  abstract public void run();
}
