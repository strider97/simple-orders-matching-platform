package com.matching.factory;

import com.matching.constants.AssetType;
import com.matching.queue.OrderQueue;

public class OrdersQueueFactory {
  OrderQueue stockOrderQueue = new OrderQueue();

  public  OrderQueue getOrderQueue(AssetType assetType) {
    switch (assetType) {
      case STOCK:
        return stockOrderQueue;
      default:
        throw new IllegalArgumentException("Unsupported asset type: " + assetType);
    }
  }
}
