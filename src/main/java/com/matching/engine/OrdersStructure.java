package com.matching.engine;

import com.matching.constants.OrderStatus;
import com.matching.pojo.Asset;
import com.matching.pojo.Order;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class OrdersStructure <T extends Asset> {
  private final Map<String, Order<T>> allOrders = new ConcurrentHashMap<>();

  public void addOrder(Order<T> order) {
    allOrders.put(order.getOrderId(), order);
  }
  public abstract List<Order<T>> match (Order<T> order);
  public abstract void removeOrder(Order<T> order);
  public void cancelOrder(String orderId) {
    allOrders.computeIfPresent(orderId, (id, order) -> {
      order.setOrderStatus(OrderStatus.CANCELLED);
      return order;
    });
  }
}
