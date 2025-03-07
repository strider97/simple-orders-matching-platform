package com.matching.pojo;

import com.matching.constants.AssetType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public abstract class Asset {
  private AssetType assetType;
  public abstract String getName();
}