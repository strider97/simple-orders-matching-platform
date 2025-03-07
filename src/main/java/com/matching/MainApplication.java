package com.matching;

import com.google.inject.Guice;
import com.matching.constants.OrderType;
import com.matching.injection.AppModule;
import com.matching.pojo.Stock;
import com.matching.pojo.request.OrderRequest;
import com.matching.services.OrderService;

import java.util.concurrent.TimeUnit;

public class MainApplication {
  public static void main(String[] args) throws InterruptedException {
    var injector = Guice.createInjector(new AppModule());
    OrderService orderService = injector.getInstance(OrderService.class);
    orderService.startWorkers();


// Stocks
    Stock appleStock = new Stock("AAPL", "Apple Inc.");
    Stock teslaStock = new Stock("TSLA", "Tesla Inc.");
    Stock googleStock = new Stock("GOOGL", "Alphabet Inc.");

// --- Apple Orders ---
    orderService.placeOrder(new OrderRequest(appleStock, OrderType.BUY, 10, 150.0));   // Buy 10 @ $150
    orderService.placeOrder(new OrderRequest(appleStock, OrderType.SELL, 5, 150.0));   // Sell 5 @ $150 (Partial match)
    orderService.placeOrder(new OrderRequest(appleStock, OrderType.SELL, 15, 155.0));  // Sell 15 @ $155 (Unmatched)
    orderService.placeOrder(new OrderRequest(appleStock, OrderType.BUY, 20, 155.0));   // Buy 20 @ $155 (Partially matches 15)

// --- Tesla Orders ---
    orderService.placeOrder(new OrderRequest(teslaStock, OrderType.BUY, 5, 700.0));   // Buy 5 @ $700
    orderService.placeOrder(new OrderRequest(teslaStock, OrderType.SELL, 3, 700.0));  // Sell 3 @ $700 (Partial match)
    orderService.placeOrder(new OrderRequest(teslaStock, OrderType.BUY, 10, 705.0));  // Buy 10 @ $705 (Waiting)
    orderService.placeOrder(new OrderRequest(teslaStock, OrderType.SELL, 5, 705.0));  // Sell 5 @ $705 (Partial match)
    orderService.placeOrder(new OrderRequest(teslaStock, OrderType.SELL, 10, 710.0)); // Sell 10 @ $710 (Unmatched)

// --- Google Orders ---
//    orderService.placeOrder(new OrderRequest(googleStock, OrderType.BUY, 4, 2800.0));
//    TimeUnit.MILLISECONDS.sleep(100);
//    orderService.placeOrder(new OrderRequest(googleStock, OrderType.BUY, 6, 2800.0));
//    TimeUnit.MILLISECONDS.sleep(100);
//    orderService.placeOrder(new OrderRequest(googleStock, OrderType.SELL, 5, 2799.0));
//    TimeUnit.MILLISECONDS.sleep(100);
//    orderService.placeOrder(new OrderRequest(googleStock, OrderType.SELL, 10, 2798.0));
//    TimeUnit.MILLISECONDS.sleep(100);

    orderService.placeOrder(new OrderRequest(googleStock, OrderType.SELL, 6, 2800.0)); // Sell 6 @ $2800
    TimeUnit.MILLISECONDS.sleep(1);

    orderService.placeOrder(new OrderRequest(googleStock, OrderType.SELL, 4, 2802.0)); // Sell 4 @ $2802 (higher price, lower priority)
    TimeUnit.MILLISECONDS.sleep(1);

    orderService.placeOrder(new OrderRequest(googleStock, OrderType.BUY, 5, 2801.0)); // Buy 5 @ $2801 (partially matches $2800)
    TimeUnit.MILLISECONDS.sleep(1);

    orderService.placeOrder(new OrderRequest(googleStock, OrderType.BUY, 2, 2803.0)); // Buy 2 @ $2803 (should match $2802 first)
    TimeUnit.MILLISECONDS.sleep(1);

    orderService.placeOrder(new OrderRequest(googleStock, OrderType.SELL, 7, 2799.0)); // Sell 7 @ $2799 (best price, highest priority)
    TimeUnit.MILLISECONDS.sleep(1);

    orderService.placeOrder(new OrderRequest(googleStock, OrderType.BUY, 10, 2802.0)); // Buy 10 @ $2802 (should match $2799, $2800, then $2802)
    TimeUnit.MILLISECONDS.sleep(1);

    orderService.placeOrder(new OrderRequest(googleStock, OrderType.SELL, 3, 2801.0)); // Sell 3 @ $2801 (should match existing Buy orders)
    TimeUnit.MILLISECONDS.sleep(1);

    orderService.placeOrder(new OrderRequest(googleStock, OrderType.BUY, 4, 2800.0)); // Buy 4 @ $2800 (may not get executed if no sellers left)
    TimeUnit.MILLISECONDS.sleep(1);



  }
}