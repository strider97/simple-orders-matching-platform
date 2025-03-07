package com.matching.engine;

import com.google.inject.Inject;
import com.matching.constants.OrderStatus;
import com.matching.constants.OrderType;
import com.matching.dao.OrderDao;
import com.matching.pojo.Order;
import lombok.AllArgsConstructor;

import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

@AllArgsConstructor
public abstract class OrdersStructure {
  @Inject
  private OrderDao orderDao;

  private final ReentrantLock lock = new ReentrantLock();
  
  public void addOrder(Order order) {
    lock.lock();
    try{
      orderDao.addOrder(order.getOrderId(), order);
    } catch (Exception e) {
      System.out.println("Faced exception while saving order: " + e);
    }
    try {
      Map<String, NavigableMap<Double, PriorityQueue<Order>>> orderBook =
          order.getOrderType() == OrderType.BUY
              ? (Map<String, NavigableMap<Double, PriorityQueue<Order>>>) getBuyOrders()
              : (Map<String, NavigableMap<Double, PriorityQueue<Order>>>) getSellOrders();

      String assetName = order.getAsset().getName();

      Comparator<Double> priceComparator = order.getOrderType() == OrderType.BUY
          ? Comparator.reverseOrder()
          : Comparator.naturalOrder();

      orderBook
          .computeIfAbsent(assetName, k -> emptyMap(priceComparator))
          .computeIfAbsent(order.getPrice(), k -> new PriorityQueue<>(Comparator.comparingLong(Order::getTimestamp)))
          .add(order);
    } finally {
      lock.unlock();
    }
  }

  
  public List<Order> match(Order order) {
    lock.lock();
    try {
      Map<String, ? extends NavigableMap<Double, PriorityQueue<Order>>> orderBook =
          order.getOrderType() == OrderType.BUY ? getSellOrders() : getBuyOrders();

      NavigableMap<Double, PriorityQueue<Order>> priceMap = orderBook.get(order.getAsset().getName());
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

  
  public void removeOrder(Order order) {
    lock.lock();
    try {

      Map<String, ? extends NavigableMap<Double, PriorityQueue<Order>>> orderBook =
          order.getOrderType() == OrderType.BUY ? getBuyOrders() : getSellOrders();

      NavigableMap<Double, PriorityQueue<Order>> priceMap = orderBook.get(order.getAsset().getName());
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
  public void cancelOrder(String orderId) {
    orderDao.cancelOrder(orderId);
  }
  
  protected abstract Map<String, ? extends NavigableMap<Double, PriorityQueue<Order>>> getSellOrders();
  protected abstract Map<String, ? extends NavigableMap<Double, PriorityQueue<Order>>> getBuyOrders();
  protected abstract NavigableMap<Double, PriorityQueue<Order>> emptyMap(Comparator<Double> priceComparator);
}
