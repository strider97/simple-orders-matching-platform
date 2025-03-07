package com.matching.injection;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.matching.engine.MatchingEngine;
import com.matching.engine.OrdersStructure;
import com.matching.engine.impl.SkipListOrdersStructure;
import com.matching.engine.impl.TreeMapOrdersStructure;
import com.matching.queue.TransactionLog;

public class MatchingEngineProvider implements Provider<MatchingEngine> {
  @Inject
  private OrdersStructure ordersStructure;

  @Override
  public MatchingEngine get() {
    return new MatchingEngine(ordersStructure, new TransactionLog());
  }
}