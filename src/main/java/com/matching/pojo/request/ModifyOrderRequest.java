package com.matching.pojo.request;

import com.matching.constants.OrderType;
import com.matching.pojo.Asset;
import lombok.Getter;

@Getter
public class ModifyOrderRequest <T extends Asset> extends PlaceOrderRequest<T>  {
  private ModifyOrderRequest(String orderId, T asset, OrderType orderType, int quantity, double price) {
    super(asset, orderType, quantity, price);
    this.orderId = orderId;
  }

  public static <T extends Asset> ModifyOrderRequest<T> from(String orderId, T asset, OrderType orderType, int quantity, double price) {
    return new ModifyOrderRequest<>(orderId, asset, orderType, quantity, price);
  }
  String orderId;
}
