package com.matching.injection;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.matching.dao.OrderDao;
import com.matching.engine.OrdersStructure;
import com.matching.engine.impl.SkipListOrdersStructure;
import com.matching.engine.impl.TreeMapOrdersStructure;

import static com.matching.constants.Constants.USE_TREE_MAP;

public class OrdersStructureProvider implements Provider<OrdersStructure> {

  @Inject
  private OrderDao orderDao;
  @Override
  public OrdersStructure get() {
    return USE_TREE_MAP
        ? new TreeMapOrdersStructure(orderDao)
        : new SkipListOrdersStructure(orderDao);
  }
}