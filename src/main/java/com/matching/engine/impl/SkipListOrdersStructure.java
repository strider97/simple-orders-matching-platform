package com.matching.engine.impl;

import com.google.inject.Inject;
import com.matching.constants.OrderStatus;
import com.matching.constants.OrderType;
import com.matching.dao.OrderDao;
import com.matching.engine.OrdersStructure;
import com.matching.pojo.Asset;
import com.matching.pojo.Order;

import java.util.*;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.locks.ReentrantLock;

public class SkipListOrdersStructure extends OrdersStructure {
  private final Map<String, ConcurrentSkipListMap<Double, PriorityQueue<Order>>> buyOrders = new HashMap<>();
  private final Map<String, ConcurrentSkipListMap<Double, PriorityQueue<Order>>> sellOrders = new HashMap<>();

  @Inject
  public SkipListOrdersStructure(OrderDao orderDao) {
    super(orderDao);
  }

  @Override
  protected Map<String, ? extends NavigableMap<Double, PriorityQueue<Order>>> getSellOrders() {
    return sellOrders;
  }

  @Override
  protected Map<String, ? extends NavigableMap<Double, PriorityQueue<Order>>> getBuyOrders() {
    return buyOrders;
  }

  @Override
  protected NavigableMap<Double, PriorityQueue<Order>> emptyMap(Comparator<Double> priceComparator) {
    return new ConcurrentSkipListMap<>(priceComparator);
  }
}


