package com.matching.pojo.request;

import com.matching.constants.OrderRequestType;
import com.matching.constants.OrderType;
import com.matching.pojo.Asset;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class PlaceOrderRequest extends OrderRequest {
  public PlaceOrderRequest(Asset asset, OrderType orderType, int quantity, double price) {
    super(asset, OrderRequestType.NEW);
    this.asset = asset;
    this.orderType = orderType;
    this.quantity = quantity;
    this.price = price;
  }
  protected Asset asset;
  protected OrderType orderType;
  protected int quantity;
  protected double price;
}