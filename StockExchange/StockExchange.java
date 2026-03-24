import java.util.*;
import java.sql.Timestamp;

enum OrderType {
    BUY, SELL
}

class Order {
    private int orderId;
    private String stockSymbol;
    private int quantity;
    private double price;
    private OrderType orderType;
    private Timestamp timestamp;

    public Order(int orderId, String stockSymbol, int quantity, double price, OrderType orderType) {
        this.orderId = orderId;
        this.stockSymbol = stockSymbol;
        this.quantity = quantity;
        this.price = price;
        this.orderType = orderType;
        this.timestamp = new Timestamp(System.currentTimeMillis());
    }

    public int getOrderId() { return orderId; }
    public String getStockSymbol() { return stockSymbol; }
    public int getQuantity() { return quantity; }
    public double getPrice() { return price; }
    public OrderType getOrderType() { return orderType; }
    public Timestamp getTimestamp() { return timestamp; }

    public void reduceQuantity(int qty) {
        this.quantity -= qty;
    }
}

class Trade {
    int buyOrderId;
    int sellOrderId;
    double price;
    int quantity;

    public Trade(int buyOrderId, int sellOrderId, double price, int quantity) {
        this.buyOrderId = buyOrderId;
        this.sellOrderId = sellOrderId;
        this.price = price;
        this.quantity = quantity;
    }

    public void print() {
        System.out.println("#" + buyOrderId + " " + price + " " + quantity + " #" + sellOrderId);
    }
}

class OrderBook {
    String stockSymbol;

    PriorityQueue<Order> buyOrders;
    PriorityQueue<Order> sellOrders;

    public OrderBook(String stockSymbol) {
        this.stockSymbol = stockSymbol;

        buyOrders = new PriorityQueue<>((o1, o2) -> {
            if (o1.getPrice() != o2.getPrice()) {
                return Double.compare(o2.getPrice(), o1.getPrice()); // higher first
            }
            return o1.getTimestamp().compareTo(o2.getTimestamp());
        });

        sellOrders = new PriorityQueue<>((o1, o2) -> {
            if (o1.getPrice() != o2.getPrice()) {
                return Double.compare(o1.getPrice(), o2.getPrice()); // lower first
            }
            return o1.getTimestamp().compareTo(o2.getTimestamp());
        });
    }

    public void addBuy(Order order) {
        buyOrders.add(order);
    }

    public void addSell(Order order) {
        sellOrders.add(order);
    }

    public Order getBestBuy() {
        return buyOrders.peek();
    }

    public Order getBestSell() {
        return sellOrders.peek();
    }

    public void removeBestBuy() {
        buyOrders.poll();
    }

    public void removeBestSell() {
        sellOrders.poll();
    }
}


interface MatchingStrategy {
    void match(Order order, OrderBook orderBook);
}


class BuyMatchingStrategy implements MatchingStrategy {

    @Override
    public void match(Order buyOrder, OrderBook orderBook) {

        while (!orderBook.sellOrders.isEmpty() &&
                orderBook.getBestSell().getPrice() <= buyOrder.getPrice()) {

            Order sellOrder = orderBook.getBestSell();

            int qty = Math.min(buyOrder.getQuantity(), sellOrder.getQuantity());

            double price = sellOrder.getPrice();

            Trade trade = new Trade(
                    buyOrder.getOrderId(),
                    sellOrder.getOrderId(),
                    price,
                    qty
            );
            trade.print();

            buyOrder.reduceQuantity(qty);
            sellOrder.reduceQuantity(qty);

            if (sellOrder.getQuantity() == 0) {
                orderBook.removeBestSell();
            }

            if (buyOrder.getQuantity() == 0) break;
        }

        if (buyOrder.getQuantity() > 0) {
            orderBook.addBuy(buyOrder);
        }
    }
}

// SELL STRATEGY
class SellMatchingStrategy implements MatchingStrategy {

    @Override
    public void match(Order sellOrder, OrderBook orderBook) {

        while (!orderBook.buyOrders.isEmpty() &&
                orderBook.getBestBuy().getPrice() >= sellOrder.getPrice()) {

            Order buyOrder = orderBook.getBestBuy();

            int qty = Math.min(sellOrder.getQuantity(), buyOrder.getQuantity());

            // ✅ ALWAYS SELL PRICE
            double price = sellOrder.getPrice();

            Trade trade = new Trade(
                    buyOrder.getOrderId(),
                    sellOrder.getOrderId(),
                    price,
                    qty
            );
            trade.print();

            sellOrder.reduceQuantity(qty);
            buyOrder.reduceQuantity(qty);

            if (buyOrder.getQuantity() == 0) {
                orderBook.removeBestBuy();
            }

            if (sellOrder.getQuantity() == 0) break;
        }

       
        if (sellOrder.getQuantity() > 0) {
            orderBook.addSell(sellOrder);
        }
    }
}

// FACTORY
class MatchingStrategyFactory {
    public static MatchingStrategy getStrategy(OrderType type) {
        if (type == OrderType.BUY) return new BuyMatchingStrategy();
        else return new SellMatchingStrategy();
    }
}

// STOCK EXCHANGE
class StockExchange {

    Map<String, OrderBook> orderBooks = new HashMap<>();

    public void placeOrder(Order order) 
    {
        OrderBook orderBook = orderBooks.computeIfAbsent(
                order.getStockSymbol(),
                k -> new OrderBook(k)
        );

        MatchingStrategy strategy = MatchingStrategyFactory.getStrategy(order.getOrderType());
        strategy.match(order, orderBook);
    }
}