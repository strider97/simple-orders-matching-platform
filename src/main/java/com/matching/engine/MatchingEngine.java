package com.matching.engine;

import com.google.inject.Inject;
import com.matching.constants.OrderType;
import com.matching.pojo.Order;
import com.matching.pojo.OrderDetails;
import com.matching.pojo.Trade;
import com.matching.pojo.Transaction;
import com.matching.queue.TransactionLog;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.*;

import static com.matching.utils.CommonUtils.isEmpty;

@AllArgsConstructor
public class MatchingEngine  {
  @Inject
  private OrdersStructure ordersStructure;
  @Inject
  private TransactionLog transactionLog;

  public synchronized void processOrder(Order order) {
    ordersStructure.addOrder(order);
    List<Order> matchedOrders = ordersStructure.match(order);
    if(matchedOrders != null && matchedOrders.size() > 0) {
      conductTrade(order, matchedOrders);
    }
  }

  public void cancelOrder(String orderId) {
    ordersStructure.cancelOrder(orderId);
  }

  public void conductTrade(Order order, List<Order> matchedOrders) {
    if (order == null || matchedOrders == null || matchedOrders.isEmpty()) {
      throw new IllegalArgumentException("Order or matched orders can't be null/empty");
    }

    Iterator<Order> iterator = matchedOrders.iterator();
    List<Trade> trades = new ArrayList<>();

    while (iterator.hasNext() && order.getQuantity() > 0) {
      Order matchedOrder = iterator.next();

      double tradePrice = order.getOrderType() == OrderType.BUY ? matchedOrder.getPrice() : order.getPrice();
      int tradeQuantity = Math.min(order.getQuantity(), matchedOrder.getQuantity());

      order.setQuantity(order.getQuantity() - tradeQuantity);
      matchedOrder.setQuantity(matchedOrder.getQuantity() - tradeQuantity);
      trades.add(createTrade(order, matchedOrder, tradeQuantity, tradePrice));

      if (!isEmpty(matchedOrder)) {
        ordersStructure.addOrder(matchedOrder);
      }
    }

    if (isEmpty(order)) {
      ordersStructure.removeOrder(order);
    }
    transactionLog.commitToLog(createTransaction(trades));
  }

  private Trade createTrade(Order order, Order matchedOrder, int tradeQuantity, double tradePrice) {
    Order buyOrder = OrderType.BUY.equals(order.getOrderType()) ? order : matchedOrder;
    Order sellOrder = OrderType.SELL.equals(order.getOrderType()) ? order : matchedOrder;
    return Trade.builder()
        .tradeId(UUID.randomUUID().toString())
        .tradePrice(tradePrice)
        .tradeQuantity(tradeQuantity)
        .buyOrderDetails(OrderDetails.from(buyOrder, tradeQuantity))
        .sellOrderDetails(OrderDetails.from(sellOrder, tradeQuantity))
        .build();
  }

  private Transaction createTransaction(List<Trade> trades) {
    return Transaction.builder()
        .transactionId(UUID.randomUUID().toString())
        .trades(trades)
        .timestamp(new Date().getTime())
        .build();
  }

  public List<Transaction> getTransactionDoneByEngine() {
    return transactionLog.getTransactions();
  }
}