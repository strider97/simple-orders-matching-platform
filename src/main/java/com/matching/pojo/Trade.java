package com.matching.pojo;

import com.matching.pojo.OrderDetails;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@Builder
public class Trade {
  String tradeId;
  double tradePrice;
  int tradeQuantity;
  OrderDetails buyOrderDetails;
  OrderDetails sellOrderDetails;

  @Override
  public String toString() {
    return "  - Trade ID: " + tradeId + "\n"
        + "    - Trade Price: " + tradePrice + "\n"
        + "    - Trade Quantity: " + tradeQuantity + "\n"
        + "    - Buy Order:\n" + buyOrderDetails.toString()
        + "    - Sell Order:\n" + sellOrderDetails.toString();
  }
}
