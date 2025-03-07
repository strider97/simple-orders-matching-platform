package com.matching.pojo;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class Transaction {
  String transactionId;
  List<Trade> trades;
  long timestamp;

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("Transaction ID: ").append(transactionId).append("\n");
    for (Trade trade : trades) {
      sb.append(trade.toString()).append("\n");
    }
    sb.append("  - Timestamp: ").append(timestamp).append("\n");
    return sb.toString();
  }
}
