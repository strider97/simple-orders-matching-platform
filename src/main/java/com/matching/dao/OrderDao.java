package com.matching.dao;

import com.matching.pojo.Order;

public interface OrderDao {
  void addRequestToOrders(String requestId, String orderId);
  void addOrder(String orderId, Order order);
  String getOrderIdForRequest(String requestId);
  Order getOrder(String orderId);
  void cancelOrder(String orderId);
}
