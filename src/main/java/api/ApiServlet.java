package api;

import com.account.TradingAccount;
import com.dao.*;
import com.market.MarketPlace;
import com.market.TradeResult;
import com.trading.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

@WebServlet("/api/account/*")
public class ApiServlet extends HttpServlet {

    private UserDAO userDAO = new UserDAO();
    private TradingAccountDAO tradingAccountDAO = new TradingAccountDAO();
    private OrderDAO orderDAO = new OrderDAO();
    private StockDAO stockDAO = new StockDAO();
    private MarketPlace marketPlace = new MarketPlace();
    private TransactionDAO transactionDAO = new TransactionDAO();
    private StockHoldingDAO stockHoldingDAO = new StockHoldingDAO();


    protected void service(HttpServletRequest req, HttpServletResponse res) throws IOException {
        res.setContentType("application/json");

        String path = req.getPathInfo();
        String method = req.getMethod();

        if(path == null) path = "";

        if(path.equals("/balance")){
                if(method.equals("GET")){
                    handleBalance(req, res);
                }
        }
        else if(path.equals("/stocks")){
                if(method.equals("GET")){
                    handleViewStocks(req, res);
                }
        }
        else if(path.equals("/orders")){
            if(method.equals("GET")){
                handleViewMyOrders(req, res);
            }
        }
        else if(path.equals("/transactions")){
            if(method.equals("GET")){
                handleViewMyTransactions(req, res);
            }
        }
        else if(path.equals("/transactions/all")){
            if(method.equals("GET")){
                handleViewAllTransactions(req, res);
            }
        }
        else if(path.equals("/portfolio")){
            if(method.equals("GET")){
                handleViewPortfolio(req, res);
            }
        }
        else if(path.equals("/orderbook")){
            if(method.equals("GET")){
                handleOrderBook(req, res);
            }
        }
        else if(path.equals("/addMoney")) {
            if (method.equals("POST")) {
                handleAddMoney(req, res);
            }
        }else if(path.equals("/delete")){
            if(method.equals("POST")){
                handleDeleteAccount(req, res);
            }
        }else if(path.equals("/buyOrder")){
            if(method.equals("POST")){
                handleBuyOrder(req, res);
            }
        }
        else if (path.equals("/myStocks") && method.equals("GET")) {
            handleMyStocks(req, res);
        }
        else if (path.equals("/sellOrder") && method.equals("POST")) {
            handleSellOrder(req, res);
        }
    }

    private void handleMyStocks(HttpServletRequest req, HttpServletResponse res) throws IOException {

        JSONObject response = new JSONObject();
        HttpSession session = req.getSession(false);

        if (session == null) {
            response.put("success", false);
            response.put("message", "Session expired");
            res.getWriter().print(response);
            return;
        }

        String username = session.getAttribute("username").toString();
        User user = userDAO.findByUsername(username);

        List<StockHolding> holdings =
                stockHoldingDAO.findByDematId(user.getDematId());

        JSONArray arr = new JSONArray();

        for (StockHolding h : holdings) {
            if (h.getAvailableQuantity() > 0) {
                JSONObject obj = new JSONObject();
                obj.put("name", StockDAO.getStockNameById(h.getStockId()));
                obj.put("qty", h.getAvailableQuantity());
                arr.put(obj);
            }
        }

        response.put("success", true);
        response.put("data", arr);
        res.getWriter().print(response);
    }

    private void handleSellOrder(HttpServletRequest req, HttpServletResponse res) throws IOException {

        JSONObject response = new JSONObject();
        HttpSession session = req.getSession(false);

        if (session == null) {
            response.put("success", false);
            response.put("message", "Session expired");
            res.getWriter().print(response);
            return;
        }

        String stockName = req.getParameter("stockName");
        String qtyStr = req.getParameter("quantity");
        String priceStr = req.getParameter("price");

        if (stockName == null || qtyStr == null || priceStr == null) {
            response.put("success", false);
            response.put("message", "Missing parameters");
            res.getWriter().print(response);
            return;
        }

        int quantity = Integer.parseInt(qtyStr);
        double price = Double.parseDouble(priceStr);

        String username = session.getAttribute("username").toString();
        User user = userDAO.findByUsername(username);

        Order order = marketPlace.placeSellOrder(
                user.getUserId(),
                stockName.toUpperCase(),
                quantity,
                price
        );

        if (order == null) {
            response.put("success", false);
            response.put("message", "Not enough stocks available");
            res.getWriter().print(response);
            return;
        }

        TradeResult t = TradeResult.lastTrade;
        int remaining = order.getQuantity();

        String status;
        if (remaining == 0) {
            status = "FILLED";
        } else if (t != null) {
            status = "PARTIALLY_FILLED";
        } else {
            status = "WAITING";
        }

        response.put("success", true);
        response.put("orderId", order.getOrderId());
        response.put("status", status);
        response.put("remaining", remaining);

        if (t != null) {
            JSONObject trade = new JSONObject();
            trade.put("buyer", t.buyer);
            trade.put("seller", t.seller);
            trade.put("stock", t.stock);
            trade.put("quantity", t.quantity);
            trade.put("price", t.price);
            trade.put("total", t.total);
            response.put("trade", trade);
        }

        TradeResult.lastTrade = null;
        res.getWriter().print(response);
    }


    private void handleBuyOrder(HttpServletRequest req, HttpServletResponse res) throws IOException {

        res.setContentType("application/json");
        JSONObject response = new JSONObject();

        HttpSession session = req.getSession(false);
        if(session == null){
            response.put("success", false);
            response.put("message", "Session expired");
            res.getWriter().print(response);
            return;
        }

        String stockName = req.getParameter("stockName");
        String qtyStr = req.getParameter("quantity");
        String priceStr = req.getParameter("price");

        if(stockName == null || qtyStr == null || priceStr == null){
            response.put("success", false);
            response.put("message", "Missing parameters");
            res.getWriter().print(response);
            return;
        }

        int quantity = Integer.parseInt(qtyStr);
        double price = Double.parseDouble(priceStr);

        String username = session.getAttribute("username").toString();
        User user = userDAO.findByUsername(username);

        Order order = marketPlace.placeBuyOrder(user.getUserId(),
                stockName.toUpperCase(), quantity, price
        );

        if(order == null){
            response.put("success", false);
            response.put("message", "Insufficient balance or invalid order");
            res.getWriter().print(response);
            return;
        }

        int remaining = order.getQuantity();
        TradeResult t = TradeResult.lastTrade;
        int tradedQty = TradeResult.lastTrade.quantity;
        order.setQuantity(order.getQuantity() - tradedQty);

        String status;
        if (remaining == 0) {
            status = "FILLED";
        } else if (TradeResult.lastTrade != null) {
            status = "PARTIALLY_FILLED";
        } else {
            status = "WAITING";
        }


        response.put("success", true);
        response.put("orderId", order.getOrderId());
        response.put("status", status);
        response.put("remaining", remaining);

        if(t != null){
            JSONObject trade = new JSONObject();
            trade.put("buyer", t.buyer);
            trade.put("seller", t.seller);
            trade.put("stock", t.stock);
            trade.put("quantity", t.quantity);
            trade.put("price", t.price);
            trade.put("total", t.total);

            response.put("trade", trade);
        }
        res.getWriter().print(response);
        TradeResult.lastTrade = null;
    }


    private void handleDeleteAccount(HttpServletRequest req, HttpServletResponse res) throws IOException {

        res.setContentType("application/json");
        JSONObject response = new JSONObject();

        HttpSession session = req.getSession(false);
        if(session == null){
            response.put("success", false);
            response.put("message", "Session expired");
            res.getWriter().print(response);
            return;
        }

        String data = req.getParameter("data");

        if(data == null || !data.equalsIgnoreCase("CONFIRM")){
            response.put("success", false);
            response.put("message", "Type CONFIRM to delete your account");
            res.getWriter().print(response);
            return;
        }

        String username = session.getAttribute("username").toString();
        User user = userDAO.findByUsername(username);

        boolean deleted = userDAO.deleteUser(user.getUserId());

        if(deleted){
            response.put("success", true);
            response.put("message", "Account deleted successfully");
            session.invalidate();
        } else {
            response.put("success", false);
            response.put("message", "User account not deleted");
        }

        res.getWriter().print(response);
    }


    private void handleViewPortfolio(HttpServletRequest req, HttpServletResponse res) throws IOException{
        JSONObject response = new JSONObject();
        HttpSession session = req.getSession(false);

        if(session == null){
            response.put("success", false);
            response.put("message", "Session expired");
            res.getWriter().print(response);
            return;
        }

        String username = session.getAttribute("username").toString();
        User user = userDAO.findByUsername(username);

        List<StockHolding> stockHoldings = stockHoldingDAO.findByDematId(user.getDematId());

        JSONArray arr = new JSONArray();
        for(StockHolding s : stockHoldings){
            JSONObject obj = new JSONObject();
            obj.put("stockName", StockDAO.getStockNameById(s.getStockId()));
            obj.put("total", s.getTotalQuantity());
            obj.put("reserved", s.getReservedQuantity());
            obj.put("available", s.getAvailableQuantity());

            arr.put(obj);
        }

        response.put("success", true);
        response.put("data", arr);

        res.getWriter().print(response);
    }

    private void handleViewAllTransactions(HttpServletRequest req, HttpServletResponse res) throws IOException{
        JSONObject response = new JSONObject();
        HttpSession session = req.getSession(false);

        if(session == null){
            response.put("success", false);
            response.put("message", "Session expired");
            res.getWriter().print(response);
            return;
        }

        List<Transaction> transactions = transactionDAO.findAll();

        JSONArray arr = new JSONArray();
        for (Transaction t : transactions){
            JSONObject obj = new JSONObject();
            obj.put("stockId", t.getTransactionId());
            obj.put("stockName", StockDAO.getStockNameById(t.getStockId()));
            obj.put("buyerName", t.getUserName(t.getBuyerId()));
            obj.put("sellerName", t.getUserName(t.getSellerId()));
            obj.put("quantity", t.getQuantity());
            obj.put("price", t.getPrice());
            obj.put("total", t.getQuantity() * t.getPrice());

            arr.put(obj);
        }
        response.put("success", true);
        response.put("data", arr);

        res.getWriter().print(response);
    }

    private void handleViewMyTransactions(HttpServletRequest req, HttpServletResponse res) throws IOException{
        JSONObject response = new JSONObject();
        HttpSession session = req.getSession(false);

        if (session == null) {
            response.put("success", false);
            response.put("message", "Session expired");
            res.getWriter().print(response);
            return;
        }

        String username = session.getAttribute("username").toString();

        User user = userDAO.findByUsername(username);
        List<Transaction> transactions = transactionDAO.findByUserId(user.getUserId());

        JSONArray jsonArray = new JSONArray();
        for (Transaction t : transactions){
            JSONObject obj = new JSONObject();
            obj.put("stockId", t.getTransactionId());
            obj.put("stockName", StockDAO.getStockNameById(t.getStockId()));
            obj.put("buyerName", t.getUserName(t.getBuyerId()));
            obj.put("sellerName", t.getUserName(t.getSellerId()));
            obj.put("quantity", t.getQuantity());
            obj.put("price", t.getPrice());
            obj.put("total", t.getQuantity() * t.getPrice());

            jsonArray.put(obj);
        }

        response.put("success", true);
        response.put("data", jsonArray);

        res.getWriter().print(response);
    }

    private void handleBalance(HttpServletRequest req, HttpServletResponse res) throws IOException {
        JSONObject response = new JSONObject();
        HttpSession session = req.getSession(false);

        if (session == null) {
            response.put("success", false);
            response.put("message", "Session expired");
            res.getWriter().print(response);
            return;
        }

        String username = session.getAttribute("username").toString();

        User user = userDAO.findByUsername(username);
        TradingAccount acc = tradingAccountDAO.findByUserId(user.getUserId());

        JSONObject balance = new JSONObject();
        balance.put("total", acc.getTotalBalance());
        balance.put("available", acc.getAvailableBalance());
        balance.put("reserved", acc.getReservedBalance());

        response.put("success", true);
        response.put("data", balance);

        res.getWriter().print(response);
    }

    private void handleViewStocks(HttpServletRequest req, HttpServletResponse res) throws IOException{
        JSONObject response = new JSONObject();
        HttpSession session = req.getSession(false);

        if(session == null){
            response.put("success", false);
            response.put("message", "Session expired");
            res.getWriter().print(response);
            return;
        }

        List<Stock> stocks = stockDAO.listAllStocks();

        JSONArray arr = new JSONArray();
        for(Stock s : stocks){
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("name", s.getStockName());
            arr.put(jsonObject);
        }
        res.getWriter().print(
                new JSONObject().put("success", true).put("data", arr)
        );
    }

    private void handleViewMyOrders(HttpServletRequest req, HttpServletResponse res) throws IOException{

        JSONObject response = new JSONObject();
        HttpSession session = req.getSession(false);

        if (session == null) {
            response.put("success", false);
            response.put("message", "Session expired");
            res.getWriter().print(response.toString());
            return;
        }

        String username = session.getAttribute("username").toString();
        User user = userDAO.findByUsername(username);

        List<Order> orders = orderDAO.findByUserId(user.getUserId());

        JSONArray arr = new JSONArray();

        for (Order o : orders) {
            JSONObject obj = new JSONObject();
            obj.put("id", o.getOrderId());
            obj.put("stock", StockDAO.getStockNameById(o.getStockId()));
            obj.put("qty", o.getQuantity());
            obj.put("price", o.getPrice());
            obj.put("type", o.isBuy() ? "BUY" : "SELL");
            arr.put(obj);
        }
        response.put("success", true);
        response.put("data", arr);

        res.setContentType("application/json");
        res.getWriter().print(response.toString());
    }

    private void handleOrderBook(HttpServletRequest req, HttpServletResponse res) throws IOException {

        JSONObject response = new JSONObject();

        String stockName = req.getParameter("stock");

        if(stockName == null){
            response.put("success", false);
            response.put("message", "Stock name required");
            res.getWriter().print(response.toString());
            return;
        }

        Stock stock = stockDAO.findByName(stockName);

        if(stock == null){
            response.put("success", false);
            response.put("message", "Stock not found");
            res.getWriter().print(response.toString());
            return;
        }

        int stockId = StockDAO.getStockIdByName(stockName);

        List<Order> buyOrders = orderDAO.getBuyOrders(stockId);
        List<Order> sellOrders = orderDAO.getSellOrders(stockId);

        JSONArray buyArr = new JSONArray();
        JSONArray sellArr = new JSONArray();

        for(Order o : buyOrders){
            JSONObject obj = new JSONObject();
            obj.put("id", o.getOrderId());
            obj.put("user", UserDAO.findUsernameById(o.getUserId()));
            obj.put("qty", o.getQuantity());
            obj.put("price", o.getPrice());
            buyArr.put(obj);
        }

        for(Order o : sellOrders){
            JSONObject obj = new JSONObject();
            obj.put("id", o.getOrderId());
            obj.put("user", UserDAO.findUsernameById(o.getUserId()));
            obj.put("qty", o.getQuantity());
            obj.put("price", o.getPrice());
            sellArr.put(obj);
        }

        response.put("success", true);
        response.put("stock", stockName);
        response.put("buyOrders", buyArr);
        response.put("sellOrders", sellArr);

        res.setContentType("application/json");
        res.getWriter().print(response.toString());
    }

    private void handleAddMoney(HttpServletRequest req, HttpServletResponse res) throws IOException {

        res.setContentType("application/json");
        JSONObject response = new JSONObject();

        HttpSession session = req.getSession(false);
        if(session == null){
            response.put("success", false);
            response.put("message", "Session expired");
            res.getWriter().print(response);
            return;
        }

        String amt = req.getParameter("amount");
        if(amt == null){
            response.put("success", false);
            response.put("message", "Amount is required");
            res.getWriter().print(response);
            return;
        }

        double amount = Double.parseDouble(amt);

        if(amount <= 0){
            response.put("success", false);
            response.put("message", "Amount must be greater than 0");
            res.getWriter().print(response);
            return;
        }

        String username = session.getAttribute("username").toString();
        User user = userDAO.findByUsername(username);

        boolean ok = tradingAccountDAO.credit(user.getUserId(), amount);

        if(!ok){
            response.put("success", false);
            response.put("message", "Failed to add money");
            res.getWriter().print(response);
            return;
        }

        TradingAccount acc = tradingAccountDAO.findByUserId(user.getUserId());

        JSONObject balance = new JSONObject();
        balance.put("total", acc.getTotalBalance());
        balance.put("available", acc.getAvailableBalance());
        balance.put("reserved", acc.getReservedBalance());

        response.put("success", true);
        response.put("message", "Money added successfully");
        response.put("data", balance);

        res.getWriter().print(response);
    }

}