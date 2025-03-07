package com.matching.pojo.request;

import com.matching.constants.OrderRequestType;
import com.matching.pojo.Asset;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CancelOrderRequest<T extends Asset> extends OrderRequest<T> {
  public String orderId;

  public CancelOrderRequest(String orderId) {
    super(null, OrderRequestType.CANCEL);
    this.orderId = orderId;
  }
}