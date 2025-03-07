package com.matching.injection;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.matching.dao.OrderDao;
import com.matching.engine.MatchingEngine;
import com.matching.engine.OrdersStructure;
import com.matching.engine.impl.TreeMapOrdersStructure;
import com.matching.queue.TransactionLog;

public class OrdersStructureProvider implements Provider<OrdersStructure> {

  @Inject
  private OrderDao orderDao;
  @Override
  public OrdersStructure get() {
    return new TreeMapOrdersStructure(orderDao);
  }
}