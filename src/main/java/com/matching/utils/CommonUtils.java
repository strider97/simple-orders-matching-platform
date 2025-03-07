package com.matching.utils;

import com.matching.pojo.Asset;
import com.matching.pojo.Order;

import java.util.Date;

public class CommonUtils {
  public static long getCurrentTimeInMS() {
    return new Date().getTime();
  }
  public static <T extends Asset> boolean isEmpty(Order<T> order) {
    return order.getQuantity() == 0;
  }
}
