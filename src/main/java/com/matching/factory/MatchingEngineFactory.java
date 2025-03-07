package com.matching.factory;

import com.google.inject.Inject;
import com.matching.constants.AssetType;
import com.matching.engine.MatchingEngine;
import com.matching.pojo.Asset;
import com.matching.pojo.Stock;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class MatchingEngineFactory {
  @Inject
  MatchingEngine stockMatchingEngine;

  public  MatchingEngine  getMatchingEngine(AssetType assetType) {
    switch (assetType) {
      case STOCK:
        return stockMatchingEngine;
      default:
        throw new IllegalArgumentException("Unsupported asset type");
    }
  }
}
