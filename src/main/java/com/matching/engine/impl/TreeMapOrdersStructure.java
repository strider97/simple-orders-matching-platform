package com.matching.engine.impl;

import com.google.inject.Inject;
import com.matching.dao.OrderDao;
import com.matching.engine.OrdersStructure;
import com.matching.pojo.Order;

import java.util.*;

public class TreeMapOrdersStructure extends OrdersStructure {
  private final Map<String, TreeMap<Double, PriorityQueue<Order>>> buyOrders = new HashMap<>();
  private final Map<String, TreeMap<Double, PriorityQueue<Order>>> sellOrders = new HashMap<>();

  @Inject
  public TreeMapOrdersStructure(OrderDao orderDao) {
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
    return new TreeMap<>(priceComparator);
  }
}

