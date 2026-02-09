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

import org.json.JSONObject;

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
        System.out.println("SESSION CREATED: " + session.getId());

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

}