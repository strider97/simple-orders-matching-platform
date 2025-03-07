package com.matching.engine.impl;

import com.matching.constants.OrderType;
import com.matching.engine.OrdersStructure;
import com.matching.pojo.Asset;
import com.matching.pojo.Order;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentSkipListMap;

public class SkipListOrdersStructure<T extends Asset> extends OrdersStructure<T> {
  private final ConcurrentMap<String, ConcurrentSkipListMap<Double, List<Order<T>>>> buyOrders = new ConcurrentHashMap<>();
  private final ConcurrentMap<String, ConcurrentSkipListMap<Double, List<Order<T>>>> sellOrders = new ConcurrentHashMap<>();

  @Override
  public void addOrder(Order<T> order) {
    ConcurrentMap<String, ConcurrentSkipListMap<Double, List<Order<T>>>> orderBook =
        order.getOrderType() == OrderType.BUY ? buyOrders : sellOrders;

    String assetName = order.getAsset().getName(); // Group by asset name

    // Determine comparator beforehand
    Comparator<Double> priceComparator = order.getOrderType() == OrderType.BUY
        ? Comparator.reverseOrder()  // Higher price first for BUY
        : Comparator.naturalOrder(); // Lower price first for SELL

    orderBook
        .computeIfAbsent(assetName, k -> new ConcurrentSkipListMap<>(priceComparator))
        .computeIfAbsent(order.getPrice(), k -> Collections.synchronizedList(new ArrayList<>()))
        .add(order);
  }

  @Override
  public List<Order<T>> match(Order<T> order) {
    ConcurrentMap<String, ConcurrentSkipListMap<Double, List<Order<T>>>> orderBook =
        order.getOrderType() == OrderType.BUY ? sellOrders : buyOrders;

    String assetName = order.getAsset().getName();

    ConcurrentSkipListMap<Double, List<Order<T>>> priceMap = orderBook.get(assetName);
    if (priceMap == null || priceMap.isEmpty()) {
      return null; // No matching orders
    }

    Double bestPrice = order.getOrderType() == OrderType.BUY ? priceMap.firstKey() : priceMap.lastKey();

    if ((order.getOrderType() == OrderType.BUY && bestPrice > order.getPrice()) ||
        (order.getOrderType() == OrderType.SELL && bestPrice < order.getPrice())) {
      return null; // No match found within price limits
    }

    List<Order<T>> matchedOrders = priceMap.get(bestPrice);
    if (matchedOrders == null || matchedOrders.isEmpty()) {
      return null;
    }

    return List.of(matchedOrders.get(0)); // Get the first matching order
  }

  @Override
  public void removeOrder(Order<T> order) {
    ConcurrentMap<String, ConcurrentSkipListMap<Double, List<Order<T>>>> orderBook =
        order.getOrderType() == OrderType.BUY ? buyOrders : sellOrders;

    String assetName = order.getAsset().getName();
    ConcurrentSkipListMap<Double, List<Order<T>>> priceMap = orderBook.get(assetName);

    if (priceMap != null) {
      List<Order<T>> ordersAtPrice = priceMap.get(order.getPrice());
      if (ordersAtPrice != null) {
        ordersAtPrice.remove(order);
        if (ordersAtPrice.isEmpty()) {
          priceMap.remove(order.getPrice());
        }
      }
    }
  }
}


