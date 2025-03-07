package com.matching.queue;

import com.matching.pojo.Transaction;
import com.matching.utils.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class TransactionLog {
  BlockingQueue<Transaction> transactionLog = new LinkedBlockingQueue<>();

  public void commitToLog(Transaction transaction) {
    transactionLog.add(transaction);
    Logger.log(transaction.toString());
  }

  public List<Transaction> getTransactions() {
    return new ArrayList<>(transactionLog);
  }
}
