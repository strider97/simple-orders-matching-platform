package com.matching.pojo.request;

import com.matching.constants.OrderType;
import com.matching.pojo.Asset;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class OrderRequest {
  private Asset asset;
  private OrderType orderType;
  private int quantity;
  private double price;
}
