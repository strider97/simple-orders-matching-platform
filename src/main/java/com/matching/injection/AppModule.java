package com.matching.injection;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.matching.engine.MatchingEngine;
import com.matching.factory.MatchingEngineFactory;
import com.matching.factory.OrdersQueueFactory;
import com.matching.queue.TransactionLog;
import com.matching.services.OrderService;

public class AppModule extends AbstractModule {
  @Override
  public void configure() {
    bind(OrderService.class).in(Singleton.class);
    bind(MatchingEngine.class).in(Singleton.class);
    bind(MatchingEngineFactory.class).in(Singleton.class);
    bind(OrdersQueueFactory.class).in(Singleton.class);
    bind(TransactionLog.class).in(Singleton.class);
  }
}
