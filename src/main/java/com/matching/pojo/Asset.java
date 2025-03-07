package com.matching.pojo;

import com.matching.constants.AssetType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
public abstract class Asset {
  private AssetType assetType;
  private String name;
  public abstract String getPrintedName();
}