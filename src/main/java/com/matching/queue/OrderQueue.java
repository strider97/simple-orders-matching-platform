package com.matching.queue;

import com.matching.pojo.Asset;
import com.matching.pojo.Order;
import com.matching.pojo.request.OrderRequest;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class OrderQueue {
  private final BlockingQueue<OrderRequest> ordersRequestsQueue = new LinkedBlockingQueue<>();

  public void addOrderRequest(OrderRequest orderRequest) {
    ordersRequestsQueue.add(orderRequest);
  }

  public OrderRequest takeOrder() {
    try {
      return ordersRequestsQueue.take();
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }
}
