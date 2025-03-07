package com.matching.engine;

import com.google.inject.Inject;
import com.matching.dao.OrderDao;
import com.matching.pojo.Order;
import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public abstract class OrdersStructure {
  @Inject
  private OrderDao orderDao;


  public void addOrder(Order order) {
    try{
      orderDao.addOrder(order.getOrderId(), order);
    } catch (Exception e) {
      System.out.println("Faced exception while saving order: " + e);
    }
  }
  public abstract List<Order> match (Order order);
  public abstract void removeOrder(Order order);
  public void cancelOrder(String orderId) {
    orderDao.cancelOrder(orderId);
  }
}
