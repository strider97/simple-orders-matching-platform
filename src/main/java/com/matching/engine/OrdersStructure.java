package com.matching.engine;

import com.matching.pojo.Asset;
import com.matching.pojo.Order;

import java.util.List;

public abstract class OrdersStructure <T extends Asset> {
  public abstract void addOrder(Order<T> order);
  public abstract List<Order<T>> match (Order<T> order);
  public abstract void removeOrder(Order<T> order);
}
