package com.matching.pojo;

import com.matching.constants.AssetType;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Stock extends Asset {
  private final String symbol;
  private String name;

  public Stock(String symbol, String name) {
    super(AssetType.STOCK);
    this.name = name;
    this.symbol = symbol;
  }

  @Override
  public String getName() {
    return symbol + "(" + name + ")";
  }
}
