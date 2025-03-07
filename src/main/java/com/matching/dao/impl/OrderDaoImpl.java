package com.matching.dao.impl;

import com.matching.constants.OrderStatus;
import com.matching.dao.OrderDao;
import com.matching.pojo.Asset;
import com.matching.pojo.Order;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class OrderDaoImpl implements OrderDao {
  Map<String, String> requestOrdersMap = new ConcurrentHashMap<>();
  Map<String, Order> ordersMap = new ConcurrentHashMap<>();

  @Override
  public void addRequestToOrders(String requestId, String orderId) {
    requestOrdersMap.put(requestId, orderId);
  }

  @Override
  public void addOrder(String orderId, Order order) {
    ordersMap.put(orderId, order);
  }

  public String getOrderIdForRequest(String requestId) {
    return requestOrdersMap.get(requestId);
  }

  public Order getOrder(String orderId) {
    return ordersMap.get(orderId);
  }

  @Override
  public void cancelOrder(String orderId) {
    ordersMap.computeIfPresent(orderId, (id, order) -> {
      order.setOrderStatus(OrderStatus.CANCELLED);
      return order;
    });
  }
}
