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

public class StockWorker extends Worker<Stock> {

  public StockWorker(OrderQueue<Stock> orderQueue, MatchingEngine<Stock> matchingEngine) {
    super(orderQueue, matchingEngine);
  }

  @Override
  public void run() {
    while(true) {
      OrderRequest<Stock> stockOrderRequest = orderQueue.takeOrder();
      switch (stockOrderRequest.getOrderRequestType()) {
        case CANCEL:
          CancelOrderRequest<Stock> cancelOrderRequest = (CancelOrderRequest<Stock>) stockOrderRequest;
          matchingEngine.cancelOrder(cancelOrderRequest.getOrderId());
          break;
        case NEW:
          PlaceOrderRequest<Stock> placeOrderRequest = (PlaceOrderRequest<Stock>) stockOrderRequest;
          Order<Stock> stockOrder = Order.from(placeOrderRequest);
          matchingEngine.processOrder(stockOrder);
          break;
        case UPDATE:
          ModifyOrderRequest<Stock> modifyOrderRequest = (ModifyOrderRequest<Stock>) stockOrderRequest;
          matchingEngine.cancelOrder(modifyOrderRequest.getOrderId());
          Order<Stock> newOrder = Order.from(modifyOrderRequest);
          matchingEngine.processOrder(newOrder);
          break;
      }
    }
  }
}
