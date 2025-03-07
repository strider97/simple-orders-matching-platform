package com.matching.utils;

import com.matching.pojo.Asset;
import com.matching.pojo.Order;

import java.util.Date;

public class CommonUtils {
  public static long getCurrentTimeInMS() {
    return new Date().getTime();
  }
  public static  boolean isEmpty(Order order) {
    return order.getQuantity() == 0;
  }
}
