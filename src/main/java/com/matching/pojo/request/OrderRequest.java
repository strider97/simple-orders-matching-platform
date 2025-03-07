package com.matching.pojo.request;

import com.matching.constants.OrderRequestType;
import com.matching.constants.OrderType;
import com.matching.pojo.Asset;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
public abstract class OrderRequest <T extends Asset> {
  protected T asset;
  protected OrderRequestType orderRequestType;
}