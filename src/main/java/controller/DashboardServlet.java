package controller;

import com.account.TradingAccount;
import com.dao.*;
import com.market.MarketPlace;
import com.market.TradeResult;
import com.trading.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

public class DashboardServlet extends HttpServlet {

    OrderDAO orderDAO = new OrderDAO();
    StockDAO stockDAO = new StockDAO();
    UserDAO userDAO = new UserDAO();
    TradingAccountDAO tradingAccountDAO = new TradingAccountDAO();
    MarketPlace marketPlace = new MarketPlace();
    TransactionDAO transactionDAO = new TransactionDAO();
    StockHoldingDAO stockHoldingDAO = new StockHoldingDAO();

    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws IOException, ServletException {

        res.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        res.setHeader("Pragma", "no-cache");
        res.setDateHeader("Expires", 0);

        HttpSession session = req.getSession(false);

        if (session == null || session.getAttribute("username") == null) {
            res.sendRedirect("index.html");
            return;
        }

        req.getRequestDispatcher("/dashboard/dashboard.html")
                .forward(req, res);
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws IOException {

        HttpSession session = req.getSession(false);

        if (session == null || session.getAttribute("username") == null) {
            res.setStatus(401);
            return;
        }

        String action = req.getParameter("action");

        if (action == null) {
            res.setContentType("text/plain");
            res.getWriter().print(session.getAttribute("username"));
            return;
        }

        String username = session.getAttribute("username").toString();

        if("balance".equals(action)) {
//            User user = userDAO.findByUsername(username);
//            TradingAccount tradingAccount = tradingAccountDAO.findByUserId(user.getUserId());
//
//            double totalBalance = tradingAccount.getTotalBalance();
//            double availableBalance = tradingAccount.getAvailableBalance();
//            double reservedBalance = tradingAccount.getReservedBalance();
//
//            res.getWriter().print("<br> Total Balance : " + totalBalance);
//            res.getWriter().print("<br> Available Balance : " + availableBalance);
//            res.getWriter().print("<br> Reserved Balance : " + reservedBalance);

            res.setContentType("application/json");
            User user = userDAO.findByUsername(username);
            if (user == null) {
                res.getWriter().print("{\"error\":\"Session expired\"}");
                return;
            }

            TradingAccount tradingAccount = tradingAccountDAO.findByUserId(user.getUserId());

            double total = tradingAccount.getTotalBalance();
            double available = tradingAccount.getAvailableBalance();
            double reserved = tradingAccount.getReservedBalance();

            String json = "{ " +
                    "\"total\": " + total + "," +
                    "\"available\": " + available + "," +
                    "\"reserved\": " + reserved + " }";

            res.getWriter().print(json);
        }

        if("addMoney".equals(action)){
            double amount = Double.parseDouble(req.getParameter("amount"));
            if (amount <= 0) {
                res.getWriter().print("Amount must be greater than 0");
                return;
            }

            User user = userDAO.findByUsername(username);
            boolean success = tradingAccountDAO.credit(user.getUserId(), amount);

            if (!success) {
                res.getWriter().print("Failed to add balance");
                return;
            }

            TradingAccount tradingAccount = tradingAccountDAO.findByUserId(user.getUserId());

            double totalBalance = tradingAccount.getTotalBalance();
            double availableBalance = tradingAccount.getAvailableBalance();
            double reservedBalance = tradingAccount.getReservedBalance();

            res.getWriter().print("Updated Balance : ");
            res.getWriter().print("<br> Total Balance : " + totalBalance);
            res.getWriter().print("<br> Available Balance : " + availableBalance);
            res.getWriter().print("<br> Reserved Balance : " + reservedBalance);
        }
        if("buyOrder".equals(action)){
            String stockName = req.getParameter("stockName").toUpperCase();
            int quantity = Integer.parseInt(req.getParameter("quantity"));
            double price = Double.parseDouble(req.getParameter("price"));

            User user = userDAO.findByUsername(username);

            Order order = marketPlace.placeBuyOrder(user.getUserId(), stockName, quantity, price);

            res.setContentType("application/json");

            if(order == null){
                res.getWriter().print("{\"success\":false}");
                return;
            }

            int remaining = order.getQuantity();
            TradeResult t = TradeResult.lastTrade;

            String status;

            if(t == null){
                status = "WAITING";
            }
            else if(remaining == 0){
                status = "FULLY_SOLD";
            }
            else{
                status = "PARTIALLY_SOLD";
            }

            String json = "{"
                    + "\"success\":true,"
                    + "\"orderId\":"+order.getOrderId()+","
                    + "\"status\":\""+status+"\","
                    + "\"remaining\":"+remaining;

            if(t != null){
                json += ",\"trade\":{"
                        + "\"buyer\":\""+t.buyer+"\","
                        + "\"seller\":\""+t.seller+"\","
                        + "\"stock\":\""+t.stock+"\","
                        + "\"quantity\":"+t.quantity+","
                        + "\"price\":"+t.price+","
                        + "\"total\":"+t.total
                        + "}";
            }

            json += "}";

            res.getWriter().print(json);

            TradeResult.lastTrade = null;
        }

        if("sellOrder".equals(action)){

            String stockName = req.getParameter("stockName").toUpperCase();
            int quantity = Integer.parseInt(req.getParameter("quantity"));
            double price = Double.parseDouble(req.getParameter("price"));

            User user = userDAO.findByUsername(username);

            Order order = marketPlace.placeSellOrder(
                    user.getUserId(), stockName, quantity, price
            );

            res.setContentType("application/json");

            if(order == null){
                res.getWriter().print("""
        {"Failed to place sell order":"Not enough stocks available"}
        """);
                return;
            }

            int remaining = order.getQuantity();
            TradeResult t = TradeResult.lastTrade;

            String status;

            if(t == null){
                status = "WAITING";
            }
            else if(remaining == 0){
                status = "FULLY_SOLD";
            }
            else{
                status = "PARTIALLY_SOLD";
            }

            String json = "{"
                    + "\"success\":true,"
                    + "\"orderId\":"+order.getOrderId()+","
                    + "\"status\":\""+status+"\","
                    + "\"remaining\":"+remaining;

            if(t != null){
                json += ",\"trade\":{"
                        + "\"buyer\":\""+t.buyer+"\","
                        + "\"seller\":\""+t.seller+"\","
                        + "\"stock\":\""+t.stock+"\","
                        + "\"quantity\":"+t.quantity+","
                        + "\"price\":"+t.price+","
                        + "\"total\":"+t.total
                        + "}";
            }

            json += "}";

            res.getWriter().print(json);

            TradeResult.lastTrade = null;
        }

        if("viewStocks".equals(action)){
            List<Stock> stocks = stockDAO.listAllStocks();
            String table = "<table border='1' cellpadding='8'>" +
                    " <tr><th>Stock Name</th></tr>";
            for (Stock s : stocks) {
                table += "<tr><td>" + s.getStockName() + "</td></tr>";
            }
            table += "</table>";
            res.getWriter().print(table);
        }

        if("viewMyOrders".equals(action)){

            User user = userDAO.findByUsername(username);
            List<Order> orders = orderDAO.findByUserId(user.getUserId());

            res.setContentType("application/json");

            StringBuilder json = new StringBuilder("[");
            for(int i=0;i<orders.size();i++){
                Order o = orders.get(i);

                json.append("{")
                        .append("\"id\":").append(o.getOrderId()).append(",")
                        .append("\"stock\":\"").append(StockDAO.getStockNameById(o.getStockId())).append("\",")
                        .append("\"qty\":").append(o.getQuantity()).append(",")
                        .append("\"price\":").append(o.getPrice()).append(",")
                        .append("\"type\":\"").append(o.isBuy()?"BUY":"SELL").append("\"")
                        .append("}");

                if(i < orders.size()-1) json.append(",");
            }
            json.append("]");

            res.getWriter().print(json.toString());
        }

        if("viewAllStockOrders".equals(action)){
            String stockName = req.getParameter("stockName");
            Stock stock = stockDAO.findByName(stockName);
            if (stock == null) {
                res.getWriter().print("Stock not found: " + stockName);
                return;
            }
            int stockId = StockDAO.getStockIdByName(stockName);
            List<Order> buyOrders = orderDAO.getBuyOrders(stockId);
            List<Order> sellOrders = orderDAO.getSellOrders(stockId);

            res.getWriter().print("<h2>ORDER BOOK: " + stockName + "</h2>");

            // BUY ORDERS
            res.getWriter().print("<h3>BUY ORDERS</h3>");
            if (buyOrders.isEmpty()) {
                res.getWriter().print("No active buy orders");
            } else {
                String table = "<table border='1' cellpadding='8'>" +
                        "<tr>" +
                        "<th>Order ID</th>" +
                        "<th>User name</th>" +
                        "<th>Quantity</th>" +
                        "<th>Price</th>" +
                        "</tr>";
                for (Order o : buyOrders) {
                    table += "<tr>";
                    table += "<td>" + o.getOrderId() + "</td>";
                    table += "<td>" + UserDAO.findUsernameById(o.getUserId()) + "</td>";
                    table += "<td>" + o.getQuantity() + "</td>";
                    table += "<td>" + o.getPrice() + "</td>";
                    table += "</tr>";
                }
                table += "</table>";
                res.getWriter().print(table);
            }

            res.getWriter().print("<hr>");

            // SELL ORDERS
            res.getWriter().print("<h3>SELL ORDERS</h3>");
            if (sellOrders.isEmpty()) {
                res.getWriter().print("No active sell orders");
            } else {
                String table = "<table border='1' cellpadding='8'>" +
                        "<tr>" +
                        "<th>Order ID</th>" +
                        "<th>User name</th>" +
                        "<th>Quantity</th>" +
                        "<th>Price</th>" +
                        "</tr>";
                for (Order o : sellOrders) {
                    table += "<tr>";
                    table += "<td>" + o.getOrderId() + "</td>";
                    table += "<td>" + UserDAO.findUsernameById(o.getUserId()) + "</td>";
                    table += "<td>" + o.getQuantity() + "</td>";
                    table += "<td>" + o.getPrice() + "</td>";
                    table += "</tr>";
                }
                table += "</table>";
                res.getWriter().print(table);
            }
        }

        if("viewMyTransactions".equals(action)){
            User user = userDAO.findByUsername(username);
            List<Transaction> transactions = transactionDAO.findByUserId(user.getUserId());
            res.getWriter().print("<h2>Your transactions</h2>");
            if (transactions.isEmpty()) {
                res.getWriter().print("No transactions found");
            } else {
                String table = "<table border='1' cellpadding='8'>" +
                        "<tr>" +
                        "<th>Transaction ID</th>" +
                        "<th>Stock Name</th>" +
                        "<th>Buyer Name</th>" +
                        "<th>Seller Name</th>" +
                        "<th>Quantity</th>" +
                        "<th>Price</th>" +
                        "<th>Total</th>" +
                        "</tr>";
                for (Transaction t : transactions) {
                    table += "<tr>";
                    table += "<td>" + t.getTransactionId() + "</td>";
                    table += "<td>" + StockDAO.getStockNameById(t.getStockId()) + "</td>";
                    table += "<td>" + t.getUserName(t.getBuyerId()) + "</td>";
                    table += "<td>" + t.getUserName(t.getSellerId()) + "</td>";
                    table += "<td>" + t.getQuantity() + "</td>";
                    table += "<td>" + t.getPrice() + "</td>";
                    table += "<td>" + (t.getQuantity()*t.getPrice()) + "</td>";
                    table += "</tr>";
                }
                table += "</table>";
                res.getWriter().print(table);
            }
        }

        if("viewAllTransactions".equals(action)){
            List<Transaction> transactions = transactionDAO.findAll();
            res.getWriter().print("<h2>Your transactions</h2>");
            if (transactions.isEmpty()) {
                res.getWriter().print("No transactions found");
            } else {
                String table = "<table border='1' cellpadding='8'>" +
                        "<tr>" +
                        "<th>Transaction ID</th>" +
                        "<th>Stock Name</th>" +
                        "<th>Buyer Name</th>" +
                        "<th>Seller Name</th>" +
                        "<th>Quantity</th>" +
                        "<th>Price</th>" +
                        "<th>Total</th>" +
                        "</tr>";
                for (Transaction t : transactions) {
                    table += "<tr>";
                    table += "<td>" + t.getTransactionId() + "</td>";
                    table += "<td>" + StockDAO.getStockNameById(t.getStockId()) + "</td>";
                    table += "<td>" + t.getUserName(t.getBuyerId()) + "</td>";
                    table += "<td>" + t.getUserName(t.getSellerId()) + "</td>";
                    table += "<td>" + t.getQuantity() + "</td>";
                    table += "<td>" + t.getPrice() + "</td>";
                    table += "<td>" + (t.getQuantity()*t.getPrice()) + "</td>";
                    table += "</tr>";
                }
                table += "</table>";
                res.getWriter().print(table);
            }
        }

        if("viewPortfolio".equals(action)){
            User user = userDAO.findByUsername(username);
            if (user == null) {
                System.out.println("User not found!");
                return;
            }

            List<StockHolding> holdings = stockHoldingDAO.findByDematId(user.getDematId());

            res.getWriter().print("<h3>STOCK PORTFOLIO</h3>");

            if (holdings.isEmpty()) {
                System.out.println("<h4>No stock holdings</h4>");
            } else {
                String table = "<table border='1' cellpadding='8'>" +
                        "<tr>" +
                        "<th>Stock Name</th>" +
                        "<th>Total</th>" +
                        "<th>Reserved</th>" +
                        "<th>Available</th>" +
                        "</tr>";
                for (StockHolding h : holdings) {
                    table += "<tr>";
                    table += "<td>" + StockDAO.getStockNameById(h.getStockId()) + "</td>";
                    table += "<td>" + h.getTotalQuantity() + "</td>";
                    table += "<td>" + h.getReservedQuantity() + "</td>";
                    table += "<td>" + h.getAvailableQuantity() + "</td>";
                    table += "</tr>";
                }
                table += "</table>";
                res.getWriter().print(table);
            }
        }

        if("deleteMyAccount".equals(action)){
            String data = req.getParameter("data").toUpperCase();

            if (data == null || !data.equalsIgnoreCase("CONFIRM")) {
                res.getWriter().print("Enter CONFIRM to delete account");
                return;
            }

            User user = userDAO.findByUsername(username);
            if(userDAO.deleteUser(user.getUserId())){
                res.getWriter().print("<h3>UserId : "+ user.getUserId() + ", account deleted!<br></h3>");
                res.getWriter().print("<h3>Demat account holdings are preserved!</h3>");
                session.invalidate();
            }
            else{
                res.getWriter().print("User account not deleted!");
            }
        }

        if("cancelOrder".equals(action)){

            res.setContentType("application/json");

            try{
                int orderId = Integer.parseInt(req.getParameter("orderId"));

                User user = userDAO.findByUsername(username);

                if(user == null){
                    res.getWriter().print("{\"success\":false,\"error\":\"User not found\"}");
                    return;
                }

                boolean ok = marketPlace.cancelOrder(user.getUserId(), orderId);

                if(ok){
                    res.getWriter().print("{\"success\":true}");
                }else{
                    res.getWriter().print("{\"success\":false,\"error\":\"Cannot cancel this order\"}");
                }

            }catch(Exception e){
                res.getWriter().print(
                        "{\"success\":false,\"error\":\"Server error while cancelling\"}"
                );
            }
        }



        if("modifyOrder".equals(action)){

            res.setContentType("application/json");

            try{
                int orderId = Integer.parseInt(req.getParameter("orderId"));
                int qty = Integer.parseInt(req.getParameter("quantity"));
                double price = Double.parseDouble(req.getParameter("price"));

                User user = userDAO.findByUsername(username);

                if(user == null){
                    res.getWriter().print("{\"success\":false,\"error\":\"User not found\"}");
                    return;
                }

                boolean ok = marketPlace.modifyOrder(
                        user.getUserId(), orderId, qty, price
                );

                if(ok){
                    res.getWriter().print("{\"success\":true}");
                }else{
                    res.getWriter().print(
                            "{\"success\":false,\"error\":\"Insufficient balance or stock\"}"
                    );
                }

            }catch(Exception e){
                res.getWriter().print(
                        "{\"success\":false,\"error\":\"Invalid input or server error\"}"
                );
            }
        }



        // for sell order (drop down menu display)
        if("myStocks".equals(action)){
            User user = userDAO.findByUsername(username);

            List<StockHolding> holdings = stockHoldingDAO.findByDematId(user.getDematId());

            StringBuilder json = new StringBuilder("[");

            for(int i=0;i<holdings.size();i++){
                StockHolding h = holdings.get(i);

                json.append("{")
                        .append("\"id\":").append(h.getStockId()).append(",")
                        .append("\"name\":\"").append(StockDAO.getStockNameById(h.getStockId())).append("\",")
                        .append("\"qty\":").append(h.getAvailableQuantity())
                        .append("}");

                if(i < holdings.size()-1) json.append(",");
            }

            json.append("]");
            res.setContentType("application/json");
            res.getWriter().print(json.toString());
        }

    }

//    protected void doPut(HttpServletRequest req, HttpServletResponse res) throws IOException{
//        res.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
//        res.setHeader("Pragma", "no-cache");
//        res.setDateHeader("Expires", 0);
//
//        HttpSession session = req.getSession();
//        if(session == null) return;
//
//        String action = req.getParameter("action");
//        String username = session.getAttribute("username").toString();
//    }
}
