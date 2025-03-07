import com.matching.constants.AssetType;
import com.matching.constants.OrderType;
import com.matching.engine.MatchingEngine;
import com.matching.factory.MatchingEngineFactory;
import com.matching.factory.OrdersQueueFactory;
import com.matching.pojo.Stock;
import com.matching.pojo.Transaction;
import com.matching.pojo.request.PlaceOrderRequest;
import com.matching.queue.OrderQueue;
import com.matching.services.OrderService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.mockito.Mockito.when;

public class OrderServiceTests {
  @Mock
  OrdersQueueFactory ordersQueueFactory;

  @Mock
  MatchingEngineFactory matchingEngineFactory;

  @InjectMocks
  OrderService orderService;


  @BeforeEach
  public void setup() {
    MockitoAnnotations.openMocks(this);
    when(ordersQueueFactory.getOrderQueue(AssetType.STOCK))
        .thenReturn(new OrderQueue<>());
    when(matchingEngineFactory.getMatchingEngine(AssetType.STOCK))
        .thenReturn(new MatchingEngine<>());
  }

  @Test
  void placeOrderTest() throws InterruptedException {
    orderService.startWorkers();
    Stock googleStock = new Stock("GOOGL", "Alphabet Inc.");
    orderService.placeOrder(new PlaceOrderRequest(googleStock, OrderType.SELL, 6, 1000.0)); // Sell 6 @ $1000
    TimeUnit.MILLISECONDS.sleep(100);

    orderService.placeOrder(new PlaceOrderRequest(googleStock, OrderType.SELL, 4, 1002.0)); // Sell 4 @ $1002 (higher price, lower priority)
    TimeUnit.MILLISECONDS.sleep(100);

    orderService.placeOrder(new PlaceOrderRequest(googleStock, OrderType.BUY, 5, 1001.0)); // Buy 5 @ $1001 (partially matches $1000)
    TimeUnit.MILLISECONDS.sleep(100);

    orderService.placeOrder(new PlaceOrderRequest(googleStock, OrderType.BUY, 2, 1003.0)); // Buy 2 @ $1003 (should match $1002 first)
    TimeUnit.MILLISECONDS.sleep(100);

    orderService.placeOrder(new PlaceOrderRequest(googleStock, OrderType.SELL, 7, 999.0)); // Sell 7 @ $2799 (best price, highest priority)
    TimeUnit.MILLISECONDS.sleep(100);

    orderService.placeOrder(new PlaceOrderRequest(googleStock, OrderType.BUY, 10, 1002.0)); // Buy 10 @ $1002 (should match $2799, $1000, then $1002)
    TimeUnit.MILLISECONDS.sleep(100);

    orderService.placeOrder(new PlaceOrderRequest(googleStock, OrderType.SELL, 3, 1001.0)); // Sell 3 @ $1001 (should match existing Buy orders)
    TimeUnit.MILLISECONDS.sleep(100);

    orderService.placeOrder(new PlaceOrderRequest(googleStock, OrderType.BUY, 4, 1000.0)); // Buy 4 @ $1000 (may not get executed if no sellers left)
    TimeUnit.MILLISECONDS.sleep(100);

    TimeUnit.MILLISECONDS.sleep(1000);
    List<Transaction> transactions = orderService.getAllTransactions(AssetType.STOCK);


    Assertions.assertEquals(3, transactions.size());
    var trades1 = transactions.get(0).getTrades();
    var trades2 = transactions.get(1).getTrades();
    var trades3 = transactions.get(2).getTrades();

    Assertions.assertEquals(1, trades1.size());
    Assertions.assertEquals(2, trades2.size());
    Assertions.assertEquals(2, trades3.size());

    Assertions.assertEquals(1000, trades1.get(0).getTradePrice());
    Assertions.assertEquals(1000, trades2.get(0).getTradePrice());
    Assertions.assertEquals(1002, trades2.get(1).getTradePrice());
    Assertions.assertEquals(999, trades3.get(0).getTradePrice());
    Assertions.assertEquals(1002, trades3.get(1).getTradePrice());

    Assertions.assertEquals(5, trades1.get(0).getTradeQuantity());
    Assertions.assertEquals(1, trades2.get(0).getTradeQuantity());
    Assertions.assertEquals(1, trades2.get(1).getTradeQuantity());
    Assertions.assertEquals(7, trades3.get(0).getTradeQuantity());
    Assertions.assertEquals(3, trades3.get(1).getTradeQuantity());
  }
}
