package com.matching.engine;

import com.google.inject.Inject;
import com.matching.constants.OrderStatus;
import com.matching.dao.OrderDao;
import com.matching.pojo.Asset;
import com.matching.pojo.Order;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class OrdersStructure <T extends Asset> {
  @Inject
  private OrderDao orderDao;

  public void addOrder(Order<T> order) {
    try{
      orderDao.addOrder(order.getOrderId(), order);
    } catch (Exception e) {
      System.out.println("Faced exception while saving order: " + e);
    }
  }
  public abstract List<Order<T>> match (Order<T> order);
  public abstract void removeOrder(Order<T> order);
  public void cancelOrder(String orderId) {
    orderDao.cancelOrder(orderId);
  }
}
