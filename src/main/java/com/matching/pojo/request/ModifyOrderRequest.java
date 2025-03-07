package com.matching.pojo.request;

import com.matching.constants.OrderType;
import com.matching.pojo.Asset;
import lombok.Getter;

@Getter
public class ModifyOrderRequest <T extends Asset> extends PlaceOrderRequest<T>  {
  private ModifyOrderRequest(String orderId, PlaceOrderRequest<T> placeOrderRequest) {
    super(placeOrderRequest.asset, placeOrderRequest.orderType, placeOrderRequest.quantity, placeOrderRequest.price);
    this.orderId = orderId;
  }

  public static <T extends Asset> ModifyOrderRequest<T> from(String orderId, PlaceOrderRequest<T> placeOrderRequest) {
    return new ModifyOrderRequest<>(orderId, placeOrderRequest);
  }
  String orderId;
}
