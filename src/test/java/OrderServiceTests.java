import com.matching.constants.AssetType;
import com.matching.constants.Constants;
import com.matching.constants.OrderType;
import com.matching.dao.OrderDao;
import com.matching.dao.impl.OrderDaoImpl;
import com.matching.engine.MatchingEngine;
import com.matching.engine.impl.SkipListOrdersStructure;
import com.matching.engine.impl.TreeMapOrdersStructure;
import com.matching.factory.MatchingEngineFactory;
import com.matching.factory.OrdersQueueFactory;
import com.matching.pojo.Order;
import com.matching.pojo.Stock;
import com.matching.pojo.Transaction;
import com.matching.pojo.request.CancelOrderRequest;
import com.matching.pojo.request.ModifyOrderRequest;
import com.matching.pojo.request.OrderRequest;
import com.matching.pojo.request.PlaceOrderRequest;
import com.matching.queue.OrderQueue;
import com.matching.queue.TransactionLog;
import com.matching.services.OrderService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

public class OrderServiceTests {
//  @Mock
  OrdersQueueFactory ordersQueueFactory;

//  @Mock
  MatchingEngineFactory matchingEngineFactory;

  OrderDao orderDao;

  OrderService orderService;


  @BeforeEach
  public void setup() {
    MockitoAnnotations.openMocks(this);
    Constants.NUM_WORKERS = 1;
    orderDao = new OrderDaoImpl();
    ordersQueueFactory = new OrdersQueueFactory();
    matchingEngineFactory = new MatchingEngineFactory(new MatchingEngine(new TreeMapOrdersStructure(orderDao), new TransactionLog()));
    orderService = new OrderService(ordersQueueFactory, matchingEngineFactory, orderDao);
  }

  @Test
  void placeOrderTest() throws InterruptedException {
    orderService.startWorkers();
    Stock googleStock = new Stock("GOOGL", "Alphabet Inc.");
    orderService.placeOrder(new PlaceOrderRequest(googleStock, OrderType.SELL, 6, 1000.0));
    orderService.placeOrder(new PlaceOrderRequest(googleStock, OrderType.SELL, 4, 1002.0));
    orderService.placeOrder(new PlaceOrderRequest(googleStock, OrderType.BUY, 5, 1001.0));
    orderService.placeOrder(new PlaceOrderRequest(googleStock, OrderType.BUY, 2, 1003.0));
    orderService.placeOrder(new PlaceOrderRequest(googleStock, OrderType.SELL, 7, 999.0));
    orderService.placeOrder(new PlaceOrderRequest(googleStock, OrderType.BUY, 10, 1002.0));
    orderService.placeOrder(new PlaceOrderRequest(googleStock, OrderType.SELL, 3, 1001.0));
    orderService.placeOrder(new PlaceOrderRequest(googleStock, OrderType.BUY, 4, 1000.0));

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
  @Test
  void modifyOrderTest() throws InterruptedException {
    orderService.startWorkers();
    Stock googleStock = new Stock("GOOGL", "Alphabet Inc.");
    orderService.placeOrder(new PlaceOrderRequest(googleStock, OrderType.SELL, 6, 1000.0));
    orderService.placeOrder(new PlaceOrderRequest(googleStock, OrderType.SELL, 4, 1002.0));
    orderService.placeOrder(new PlaceOrderRequest(googleStock, OrderType.BUY, 5, 1010.0));
    PlaceOrderRequest placeOrderRequest = new PlaceOrderRequest(googleStock, OrderType.BUY, 2, 900.0);

    String requestId = orderService.placeOrder(placeOrderRequest);
    TimeUnit.MILLISECONDS.sleep(400);
    Order order = orderService.getOrderDetails(requestId);
    ModifyOrderRequest modifiedOrderRequest = ModifyOrderRequest.from(
        order.getOrderId(),
        placeOrderRequest.toBuilder().quantity(7).price(1008.0).build()
    );
    orderService.placeOrder(modifiedOrderRequest);
    orderService.placeOrder(new PlaceOrderRequest(googleStock, OrderType.SELL, 7, 1005.0));
    TimeUnit.MILLISECONDS.sleep(1000);

    List<Transaction> transactions = orderService.getAllTransactions(AssetType.STOCK);

    Assertions.assertEquals(3, transactions.size());
    var trades1 = transactions.get(0).getTrades();
    var trades2 = transactions.get(1).getTrades();
    var trades3 = transactions.get(2).getTrades();

    Assertions.assertEquals(1, trades1.size());
    Assertions.assertEquals(2, trades2.size());
    Assertions.assertEquals(1, trades3.size());

    Assertions.assertEquals(1000, trades1.get(0).getTradePrice());
    Assertions.assertEquals(1000, trades2.get(0).getTradePrice());
    Assertions.assertEquals(1002, trades2.get(1).getTradePrice());
    Assertions.assertEquals(1005, trades3.get(0).getTradePrice());

    Assertions.assertEquals(5, trades1.get(0).getTradeQuantity());
    Assertions.assertEquals(1, trades2.get(0).getTradeQuantity());
    Assertions.assertEquals(4, trades2.get(1).getTradeQuantity());
    Assertions.assertEquals(2, trades3.get(0).getTradeQuantity());
  }
  @Test
  void deleteOrderTest() throws InterruptedException {
    orderService.startWorkers();
    Stock googleStock = new Stock("GOOGL", "Alphabet Inc.");
    orderService.placeOrder(new PlaceOrderRequest(googleStock, OrderType.SELL, 6, 1000.0));
    orderService.placeOrder(new PlaceOrderRequest(googleStock, OrderType.SELL, 4, 1002.0));
    orderService.placeOrder(new PlaceOrderRequest(googleStock, OrderType.BUY, 5, 1010.0));

    PlaceOrderRequest placeOrderRequest = new PlaceOrderRequest(googleStock, OrderType.BUY, 15, 900.0);
    String requestId = orderService.placeOrder(placeOrderRequest);
    TimeUnit.MILLISECONDS.sleep(400);
    Order order = orderService.getOrderDetails(requestId);
    CancelOrderRequest cancelOrderRequest =
        CancelOrderRequest.from(order.getOrderId(), new Stock("", ""));
    orderService.placeOrder(cancelOrderRequest);

    orderService.placeOrder(new PlaceOrderRequest(googleStock, OrderType.SELL, 17, 800.0));
    TimeUnit.MILLISECONDS.sleep(1000);

    List<Transaction> transactions = orderService.getAllTransactions(AssetType.STOCK);

    Assertions.assertEquals(1, transactions.size());
    var trades1 = transactions.get(0).getTrades();

    Assertions.assertEquals(1, trades1.size());

    Assertions.assertEquals(1000, trades1.get(0).getTradePrice());

    Assertions.assertEquals(5, trades1.get(0).getTradeQuantity());
  }

  @Test
  void placeOrderSkipListTest() throws InterruptedException {
    mockOrdersStructureSkipList();
    orderService.startWorkers();
    Stock googleStock = new Stock("GOOGL", "Alphabet Inc.");
    orderService.placeOrder(new PlaceOrderRequest(googleStock, OrderType.SELL, 6, 1000.0));
    orderService.placeOrder(new PlaceOrderRequest(googleStock, OrderType.SELL, 4, 1002.0));
    orderService.placeOrder(new PlaceOrderRequest(googleStock, OrderType.BUY, 5, 1001.0));
    orderService.placeOrder(new PlaceOrderRequest(googleStock, OrderType.BUY, 2, 1003.0));
    orderService.placeOrder(new PlaceOrderRequest(googleStock, OrderType.SELL, 7, 999.0));
    orderService.placeOrder(new PlaceOrderRequest(googleStock, OrderType.BUY, 10, 1002.0));
    orderService.placeOrder(new PlaceOrderRequest(googleStock, OrderType.SELL, 3, 1001.0));
    orderService.placeOrder(new PlaceOrderRequest(googleStock, OrderType.BUY, 4, 1000.0));

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
  @Test
  void modifyOrderSkipListTest() throws InterruptedException {
    mockOrdersStructureSkipList();
    orderService.startWorkers();
    Stock googleStock = new Stock("GOOGL", "Alphabet Inc.");
    orderService.placeOrder(new PlaceOrderRequest(googleStock, OrderType.SELL, 6, 1000.0));
    orderService.placeOrder(new PlaceOrderRequest(googleStock, OrderType.SELL, 4, 1002.0));
    orderService.placeOrder(new PlaceOrderRequest(googleStock, OrderType.BUY, 5, 1010.0));
    PlaceOrderRequest placeOrderRequest = new PlaceOrderRequest(googleStock, OrderType.BUY, 2, 900.0);

    String requestId = orderService.placeOrder(placeOrderRequest);
    TimeUnit.MILLISECONDS.sleep(400);
    Order order = orderService.getOrderDetails(requestId);
    ModifyOrderRequest modifiedOrderRequest = ModifyOrderRequest.from(
        order.getOrderId(),
        placeOrderRequest.toBuilder().quantity(7).price(1008.0).build()
    );
    orderService.placeOrder(modifiedOrderRequest);
    orderService.placeOrder(new PlaceOrderRequest(googleStock, OrderType.SELL, 7, 1005.0));
    TimeUnit.MILLISECONDS.sleep(1000);

    List<Transaction> transactions = orderService.getAllTransactions(AssetType.STOCK);

    Assertions.assertEquals(3, transactions.size());
    var trades1 = transactions.get(0).getTrades();
    var trades2 = transactions.get(1).getTrades();
    var trades3 = transactions.get(2).getTrades();

    Assertions.assertEquals(1, trades1.size());
    Assertions.assertEquals(2, trades2.size());
    Assertions.assertEquals(1, trades3.size());

    Assertions.assertEquals(1000, trades1.get(0).getTradePrice());
    Assertions.assertEquals(1000, trades2.get(0).getTradePrice());
    Assertions.assertEquals(1002, trades2.get(1).getTradePrice());
    Assertions.assertEquals(1005, trades3.get(0).getTradePrice());

    Assertions.assertEquals(5, trades1.get(0).getTradeQuantity());
    Assertions.assertEquals(1, trades2.get(0).getTradeQuantity());
    Assertions.assertEquals(4, trades2.get(1).getTradeQuantity());
    Assertions.assertEquals(2, trades3.get(0).getTradeQuantity());
  }

  private void mockOrdersStructureSkipList() {
    matchingEngineFactory = new MatchingEngineFactory(new MatchingEngine(new TreeMapOrdersStructure(orderDao), new TransactionLog()));
    orderService = new OrderService(ordersQueueFactory, matchingEngineFactory, orderDao);  }
}
