package com.matching.engine.impl;

import com.matching.constants.OrderType;
import com.matching.engine.OrdersStructure;
import com.matching.pojo.Asset;
import com.matching.pojo.Order;

import java.util.*;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.locks.ReentrantLock;

public class SkipListOrdersStructure<T extends Asset> extends OrdersStructure<T> {
  private final Map<String, ConcurrentSkipListMap<Double, PriorityQueue<Order<T>>>> buyOrders = new HashMap<>();
  private final Map<String, ConcurrentSkipListMap<Double, PriorityQueue<Order<T>>>> sellOrders = new HashMap<>();
  private final ReentrantLock lock = new ReentrantLock();

  @Override
  public void addOrder(Order<T> order) {
    lock.lock();
    super.addOrder(order);
    try {
      Map<String, ConcurrentSkipListMap<Double, PriorityQueue<Order<T>>>> orderBook =
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
  public List<Order<T>> match(Order<T> order) {
    lock.lock();
    try {
      Map<String, ConcurrentSkipListMap<Double, PriorityQueue<Order<T>>>> orderBook =
          order.getOrderType() == OrderType.BUY ? sellOrders : buyOrders;

      ConcurrentSkipListMap<Double, PriorityQueue<Order<T>>> priceMap = orderBook.get(order.getAsset().getName());
      if (priceMap == null) return Collections.emptyList();

      List<Order<T>> matchedOrders = new ArrayList<>();
      int remainingQuantity = order.getQuantity();

      Map.Entry<Double, PriorityQueue<Order<T>>> priceEntry = priceMap.firstEntry();

      while (priceEntry != null && remainingQuantity > 0) {
        Double currentPrice = priceEntry.getKey();
        PriorityQueue<Order<T>> orderQueue = priceEntry.getValue();

        while (!orderQueue.isEmpty() && remainingQuantity > 0) {
          Order<T> topOrder = orderQueue.peek();

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
  public void removeOrder(Order<T> order) {
    lock.lock();
    try {
      Map<String, ConcurrentSkipListMap<Double, PriorityQueue<Order<T>>>> orderBook =
          order.getOrderType() == OrderType.BUY ? buyOrders : sellOrders;

      ConcurrentSkipListMap<Double, PriorityQueue<Order<T>>> priceMap = orderBook.get(order.getAsset().getName());
      if (priceMap == null) return;

      PriorityQueue<Order<T>> orders = priceMap.get(order.getPrice());
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


