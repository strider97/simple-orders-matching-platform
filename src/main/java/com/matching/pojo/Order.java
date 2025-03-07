package com.matching.pojo;

import com.matching.constants.OrderStatus;
import com.matching.constants.OrderType;
import com.matching.pojo.request.OrderRequest;
import com.matching.pojo.request.PlaceOrderRequest;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import org.checkerframework.checker.units.qual.A;

import java.time.Instant;
import java.util.UUID;

import static com.matching.utils.CommonUtils.getCurrentTimeInMS;

@Data
@Builder
@ToString
public class Order  {
  private String orderId;
  private Asset asset;
  private OrderType orderType;
  private int quantity;
  private double price;
  private long timestamp;
  private OrderStatus orderStatus;

  public static Order from(PlaceOrderRequest orderRequest) {
    return Order.builder()
        .orderId(UUID.randomUUID().toString())
        .asset(orderRequest.getAsset())
        .orderType(orderRequest.getOrderType())
        .quantity(orderRequest.getQuantity())
        .price(orderRequest.getPrice())
        .timestamp(getCurrentTimeInMS())
        .orderStatus(OrderStatus.PENDING)
        .build();
  }
}
