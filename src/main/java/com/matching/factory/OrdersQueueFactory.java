package com.matching.factory;

import com.matching.constants.AssetType;
import com.matching.pojo.Asset;
import com.matching.pojo.Stock;
import com.matching.queue.OrderQueue;

public class OrdersQueueFactory {
  OrderQueue<Stock> stockOrderQueue = new OrderQueue<>();
  OrderQueue<Stock> currencyQueue = new OrderQueue<>();

  public <T extends Asset> OrderQueue<T> getOrderQueue(AssetType assetType) {
    switch (assetType) {
      case STOCK:
        return (OrderQueue<T>) stockOrderQueue;
      case CURRENCY:
        return (OrderQueue<T>) currencyQueue;
      default:
        throw new IllegalArgumentException("Unsupported asset type: " + assetType);
    }
  }
}
