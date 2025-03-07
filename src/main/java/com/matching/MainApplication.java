package com.matching;

import com.google.inject.Guice;
import com.matching.constants.OrderType;
import com.matching.injection.AppModule;
import com.matching.pojo.Stock;
import com.matching.pojo.request.OrderRequest;
import com.matching.pojo.request.PlaceOrderRequest;
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
    orderService.placeOrder(new PlaceOrderRequest(appleStock, OrderType.BUY, 10, 150.0));
    orderService.placeOrder(new PlaceOrderRequest(appleStock, OrderType.SELL, 5, 150.0));
    orderService.placeOrder(new PlaceOrderRequest(appleStock, OrderType.SELL, 15, 155.0));
    orderService.placeOrder(new PlaceOrderRequest(appleStock, OrderType.BUY, 20, 155.0));

// --- Tesla Orders ---
    orderService.placeOrder(new PlaceOrderRequest(teslaStock, OrderType.BUY, 5, 700.0));
    orderService.placeOrder(new PlaceOrderRequest(teslaStock, OrderType.SELL, 3, 700.0));
    orderService.placeOrder(new PlaceOrderRequest(teslaStock, OrderType.BUY, 10, 705.0));
    orderService.placeOrder(new PlaceOrderRequest(teslaStock, OrderType.SELL, 5, 705.0));
    orderService.placeOrder(new PlaceOrderRequest(teslaStock, OrderType.SELL, 10, 710.0));

// --- Google Orders ---
    orderService.placeOrder(new PlaceOrderRequest(googleStock, OrderType.BUY, 4, 2800.0));
    TimeUnit.MILLISECONDS.sleep(100);
    orderService.placeOrder(new PlaceOrderRequest(googleStock, OrderType.BUY, 6, 2800.0));
    TimeUnit.MILLISECONDS.sleep(100);
    orderService.placeOrder(new PlaceOrderRequest(googleStock, OrderType.SELL, 5, 2799.0));
    TimeUnit.MILLISECONDS.sleep(100);
    orderService.placeOrder(new PlaceOrderRequest(googleStock, OrderType.SELL, 10, 2798.0));
    TimeUnit.MILLISECONDS.sleep(100);

    orderService.placeOrder(new PlaceOrderRequest(googleStock, OrderType.SELL, 6, 2800.0));
    TimeUnit.MILLISECONDS.sleep(100);

    orderService.placeOrder(new PlaceOrderRequest(googleStock, OrderType.SELL, 4, 2802.0));
    TimeUnit.MILLISECONDS.sleep(100);

    orderService.placeOrder(new PlaceOrderRequest(googleStock, OrderType.BUY, 5, 2801.0));
    TimeUnit.MILLISECONDS.sleep(100);

    orderService.placeOrder(new PlaceOrderRequest(googleStock, OrderType.BUY, 2, 2803.0));
    TimeUnit.MILLISECONDS.sleep(100);

    orderService.placeOrder(new PlaceOrderRequest(googleStock, OrderType.SELL, 7, 2799.0));
    TimeUnit.MILLISECONDS.sleep(100);

    orderService.placeOrder(new PlaceOrderRequest(googleStock, OrderType.BUY, 10, 2802.0));
    TimeUnit.MILLISECONDS.sleep(100);

    orderService.placeOrder(new PlaceOrderRequest(googleStock, OrderType.SELL, 3, 2801.0));
    TimeUnit.MILLISECONDS.sleep(100);

    orderService.placeOrder(new PlaceOrderRequest(googleStock, OrderType.BUY, 4, 2800.0));
    TimeUnit.MILLISECONDS.sleep(100);
  }
}