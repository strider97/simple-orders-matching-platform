package com.matching.factory;

import com.matching.constants.AssetType;
import com.matching.engine.MatchingEngine;
import com.matching.pojo.Asset;
import com.matching.pojo.Stock;

public class MatchingEngineFactory {
  MatchingEngine<Stock> stockMatchingEngine = new MatchingEngine<>();
  MatchingEngine<Stock> currencyMatchingEngine = new MatchingEngine<>();

  public <T extends Asset> MatchingEngine <T> getMatchingEngine(AssetType assetType) {
    switch (assetType) {
      case STOCK:
        return (MatchingEngine<T>) stockMatchingEngine;
      case CURRENCY:
        return (MatchingEngine<T>) currencyMatchingEngine;
      default:
        return null;
    }
  }
}
