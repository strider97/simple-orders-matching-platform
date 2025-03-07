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
  private final ReentrantLock lock = new ReentrantLock();

  @Inject
  public SkipListOrdersStructure(OrderDao orderDao) {
    super(orderDao);
  }

  @Override
  public void addOrder(Order order) {
    lock.lock();
    super.addOrder(order);
    try {
      Map<String, ConcurrentSkipListMap<Double, PriorityQueue<Order>>> orderBook =
          order.getOrderType() == OrderType.BUY ? buyOrders : sellOrders;

      String assetName = order.getAsset().getName();

      Comparator<Double> priceComparator = order.getOrderType() == OrderType.BUY
          ? Comparator.reverseOrder()
          : Comparator.naturalOrder();

      orderBook
          .computeIfAbsent(assetName, k -> new ConcurrentSkipListMap<>(priceComparator))
          .computeIfAbsent(order.getPrice(), k -> new PriorityQueue<>(Comparator.comparingLong(Order::getTimestamp)))
          .add(order);
    } finally {
      lock.unlock();
    }
  }


  @Override
  public List<Order> match(Order order) {
    lock.lock();
    try {
      Map<String, ConcurrentSkipListMap<Double, PriorityQueue<Order>>> orderBook =
          order.getOrderType() == OrderType.BUY ? sellOrders : buyOrders;

      ConcurrentSkipListMap<Double, PriorityQueue<Order>> priceMap = orderBook.get(order.getAsset().getName());
      if (priceMap == null) return Collections.emptyList();

      List<Order> matchedOrders = new ArrayList<>();
      int remainingQuantity = order.getQuantity();

      Map.Entry<Double, PriorityQueue<Order>> priceEntry = priceMap.firstEntry();

      while (priceEntry != null && remainingQuantity > 0) {
        Double currentPrice = priceEntry.getKey();
        PriorityQueue<Order> orderQueue = priceEntry.getValue();

        while (!orderQueue.isEmpty() && remainingQuantity > 0) {
          Order topOrder = orderQueue.peek();
          if(OrderStatus.CANCELLED.equals(topOrder.getOrderStatus())) {
            orderQueue.poll();
            continue;
          }
          if ((order.getOrderType() == OrderType.BUY && topOrder.getPrice() > order.getPrice()) ||
              (order.getOrderType() == OrderType.SELL && topOrder.getPrice() < order.getPrice())) {
            return matchedOrders;
          }

          orderQueue.poll();
          matchedOrders.add(topOrder);
          remainingQuantity -= topOrder.getQuantity();
        }
        priceEntry = priceMap.higherEntry(currentPrice);
      }

      return matchedOrders;
    } finally {
      lock.unlock();
    }
  }


  @Override
  public void removeOrder(Order order) {
    lock.lock();
    try {
      Map<String, ConcurrentSkipListMap<Double, PriorityQueue<Order>>> orderBook =
          order.getOrderType() == OrderType.BUY ? buyOrders : sellOrders;

      ConcurrentSkipListMap<Double, PriorityQueue<Order>> priceMap = orderBook.get(order.getAsset().getName());
      if (priceMap == null) return;

      PriorityQueue<Order> orders = priceMap.get(order.getPrice());
      if (orders != null) {
        orders.remove(order);
        if (orders.isEmpty()) {
          priceMap.remove(order.getPrice());
        }
      }

      if (priceMap.isEmpty()) {
        orderBook.remove(order.getAsset().getName());
      }
    } finally {
      lock.unlock();
    }
  }
}


