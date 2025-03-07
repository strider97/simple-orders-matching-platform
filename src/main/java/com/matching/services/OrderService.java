package com.matching.services;

import com.google.inject.Inject;
import com.matching.constants.AssetType;
import com.matching.factory.MatchingEngineFactory;
import com.matching.factory.OrdersQueueFactory;
import com.matching.pojo.Order;
import com.matching.pojo.Transaction;
import com.matching.pojo.request.OrderRequest;
import com.matching.queue.TransactionLog;
import com.matching.workers.StockWorker;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.matching.constants.Constants.NUM_WORKERS;
import static com.matching.utils.CommonUtils.getCurrentTimeInMS;

public class OrderService {

  @Inject
  OrdersQueueFactory ordersQueueFactory;
  @Inject
  MatchingEngineFactory matchingEngineFactory;
  ExecutorService executorService = Executors.newSingleThreadExecutor();

  public void placeOrder(OrderRequest orderRequest) {
    ordersQueueFactory.getOrderQueue(orderRequest.getAsset().getAssetType())
        .addOrder(buildOrder(
            orderRequest
        ));
  }

  public void modifyOrder(OrderRequest orderRequest) {

  }

  public void cancelOrder(String orderId) {

  }

  public List<Transaction> getAllTransactions(AssetType assetType) {
    return matchingEngineFactory.getMatchingEngine(assetType)
        .getTransactionDoneByEngine();
  }

  private Order buildOrder(OrderRequest orderRequest) {
    return Order.builder()
        .orderId(UUID.randomUUID().toString())
        .asset(orderRequest.getAsset())
        .orderType(orderRequest.getOrderType())
        .quantity(orderRequest.getQuantity())
        .price(orderRequest.getPrice())
        .timestamp(getCurrentTimeInMS())
        .build();
  }

  public void startWorkers() {
    for(int i = 0;i<NUM_WORKERS; i++) {
      executorService.submit(
          new StockWorker(
              ordersQueueFactory.getOrderQueue(AssetType.STOCK),
              matchingEngineFactory.getMatchingEngine(AssetType.STOCK))
      );
    }
  }
}
