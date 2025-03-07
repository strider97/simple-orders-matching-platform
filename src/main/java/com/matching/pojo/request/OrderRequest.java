package com.matching.pojo.request;

import com.matching.constants.OrderRequestType;
import com.matching.constants.OrderType;
import com.matching.pojo.Asset;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
public abstract class OrderRequest  {
  protected OrderRequest(Asset asset, OrderRequestType orderRequestType) {
    this.asset = asset;
    this.orderRequestType = orderRequestType;
    requestId = UUID.randomUUID().toString();
  }
  protected Asset asset;
  protected String requestId;
  protected OrderRequestType orderRequestType;
}