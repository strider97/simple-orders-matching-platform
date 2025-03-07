package com.matching.pojo;

import com.matching.constants.OrderType;
import lombok.Data;
import lombok.ToString;

@Data
public class OrderDetails {
  private String orderId;
  OrderType orderType;
  Asset asset;
  int quantityTraded;
  double orderPrice;

  private OrderDetails(Order order, int quantityTraded) {
    this.orderId = order.getOrderId();
    this.orderType = order.getOrderType();
    this.asset = order.getAsset();
    this.quantityTraded = quantityTraded;
    this.orderPrice = order.getPrice();
  }

  public static OrderDetails from(Order order, int quantityTraded) {
    return new OrderDetails(order, quantityTraded);
  }

  @Override
  public String toString() {
    return "      - Order ID: " + orderId + "\n"
        + "      - Type: " + orderType + "\n"
        + "      - Asset: " + asset + "\n"
        + "      - Quantity Traded: " + quantityTraded + "\n"
        + "      - Order Price: " + orderPrice + "\n";
  }
}
