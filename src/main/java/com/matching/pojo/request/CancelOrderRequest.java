package com.matching.pojo.request;

import com.matching.constants.OrderRequestType;
import com.matching.pojo.Asset;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@Getter
public class CancelOrderRequest extends OrderRequest {
  public String orderId;
  private CancelOrderRequest(String orderId, Asset asset) {
    super(asset, OrderRequestType.CANCEL);
    this.orderId = orderId;
  }

  public static CancelOrderRequest from(String orderId, Asset asset) {
    return new CancelOrderRequest(orderId, asset);
  }
}