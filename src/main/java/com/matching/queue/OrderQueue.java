package com.matching.queue;

import com.matching.pojo.Asset;
import com.matching.pojo.Order;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class OrderQueue <T extends Asset>{
  private final BlockingQueue<Order<T>> ordersQueue = new LinkedBlockingQueue<>();

  public void addOrder(Order<T> order) {
    ordersQueue.add(order);
  }

  public Order<T> takeOrder() {
    try {
      return ordersQueue.take();
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }
}
