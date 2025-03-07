package com.matching.pojo.request;

import com.matching.constants.OrderRequestType;
import com.matching.constants.OrderType;
import com.matching.pojo.Asset;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
public abstract class OrderRequest <T extends Asset> {
  protected OrderRequest(T asset, OrderRequestType orderRequestType) {
    this.asset = asset;
    this.orderRequestType = orderRequestType;
  }
  protected T asset;
  protected String requestId;
  protected OrderRequestType orderRequestType;

  public synchronized String getRequestId() {
    if(requestId == null) {
      requestId = UUID.randomUUID().toString();
    }
    return requestId;
  }
}