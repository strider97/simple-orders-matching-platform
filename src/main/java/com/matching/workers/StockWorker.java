package com.matching.workers;

import com.matching.constants.OrderRequestType;
import com.matching.engine.MatchingEngine;
import com.matching.pojo.Order;
import com.matching.pojo.Stock;
import com.matching.pojo.request.CancelOrderRequest;
import com.matching.pojo.request.ModifyOrderRequest;
import com.matching.pojo.request.OrderRequest;
import com.matching.pojo.request.PlaceOrderRequest;
import com.matching.queue.OrderQueue;

public class StockWorker extends Worker {

  public StockWorker(OrderQueue orderQueue, MatchingEngine matchingEngine) {
    super(orderQueue, matchingEngine);
  }

  @Override
  public void run() {
    while(true) {
      OrderRequest stockOrderRequest = orderQueue.takeOrder();
      switch (stockOrderRequest.getOrderRequestType()) {
        case CANCEL:
          CancelOrderRequest cancelOrderRequest = (CancelOrderRequest) stockOrderRequest;
          matchingEngine.cancelOrder(cancelOrderRequest.getOrderId());
          break;
        case NEW:
          PlaceOrderRequest placeOrderRequest = (PlaceOrderRequest) stockOrderRequest;
          Order stockOrder = Order.from(placeOrderRequest);
          matchingEngine.processOrder(stockOrder);
          break;
        case UPDATE:
          ModifyOrderRequest modifyOrderRequest = (ModifyOrderRequest) stockOrderRequest;
          matchingEngine.cancelOrder(modifyOrderRequest.getOrderId());
          Order newOrder = Order.from(modifyOrderRequest);
          matchingEngine.processOrder(newOrder);
          break;
      }
    }
  }
}
