package com.matching.services;

import com.google.inject.Inject;
import com.matching.constants.AssetType;
import com.matching.dao.OrderDao;
import com.matching.factory.MatchingEngineFactory;
import com.matching.factory.OrdersQueueFactory;
import com.matching.pojo.Order;
import com.matching.pojo.Transaction;
import com.matching.pojo.request.CancelOrderRequest;
import com.matching.pojo.request.ModifyOrderRequest;
import com.matching.pojo.request.OrderRequest;
import com.matching.pojo.request.PlaceOrderRequest;
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

  @Inject
  OrderDao orderDao;
  ExecutorService executorService = Executors.newSingleThreadExecutor();

  public String placeOrder(OrderRequest orderRequest) {
    ordersQueueFactory.getOrderQueue(orderRequest.getAsset().getAssetType())
        .addOrderRequest(orderRequest);
    return orderRequest.getRequestId();
  }

  public List<Transaction> getAllTransactions(AssetType assetType) {
    return matchingEngineFactory.getMatchingEngine(assetType)
        .getTransactionDoneByEngine();
  }

  public Order getOrderDetails(String requestId) {
    String orderId = orderDao.getOrderIdForRequest(requestId);
    return orderDao.getOrder(orderId);
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
