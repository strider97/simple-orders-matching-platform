package com.matching.pojo.request;

import com.matching.constants.OrderType;
import com.matching.pojo.Asset;
import lombok.Getter;

@Getter
public class ModifyOrderRequest  extends PlaceOrderRequest  {
  private ModifyOrderRequest(String orderId, PlaceOrderRequest placeOrderRequest) {
    super(placeOrderRequest.asset, placeOrderRequest.orderType, placeOrderRequest.quantity, placeOrderRequest.price);
    this.orderId = orderId;
  }

  public static  ModifyOrderRequest from(String orderId, PlaceOrderRequest placeOrderRequest) {
    return new ModifyOrderRequest(orderId, placeOrderRequest);
  }
  String orderId;
}
