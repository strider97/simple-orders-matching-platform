package com.matching.injection;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;
import com.matching.dao.OrderDao;
import com.matching.dao.impl.OrderDaoImpl;
import com.matching.engine.MatchingEngine;
import com.matching.engine.OrdersStructure;
import com.matching.engine.impl.SkipListOrdersStructure;
import com.matching.engine.impl.TreeMapOrdersStructure;
import com.matching.factory.MatchingEngineFactory;
import com.matching.factory.OrdersQueueFactory;
import com.matching.pojo.Order;
import com.matching.queue.TransactionLog;
import com.matching.services.OrderService;

public class AppModule extends AbstractModule {
  @Override
  public void configure() {
    bind(OrderService.class).in(Singleton.class);
    bind(MatchingEngineFactory.class).in(Singleton.class);
    bind(OrdersQueueFactory.class).in(Singleton.class);
    bind(TransactionLog.class).in(Singleton.class);
    bind(OrderDao.class).to(OrderDaoImpl.class);
    bind(OrderDaoImpl.class).in(Singleton.class);
    bind(MatchingEngine.class).toProvider(MatchingEngineProvider.class);
    bind(OrdersStructure.class).toProvider(OrdersStructureProvider.class);
  }
}
