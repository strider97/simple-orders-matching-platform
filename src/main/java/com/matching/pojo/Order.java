package com.matching.pojo;

import com.matching.constants.OrderStatus;
import com.matching.constants.OrderType;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.time.Instant;

@Data
@Builder
@ToString
public class Order <T extends Asset> {
  private String orderId;
  private T asset;
  private OrderType orderType;
  private int quantity;
  private double price;
  private long timestamp;
}
