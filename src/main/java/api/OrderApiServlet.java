package api;

import com.dao.OrderDAO;
import com.dao.StockDAO;
import com.dao.StockHoldingDAO;
import com.dao.UserDAO;
import com.market.MarketPlace;
import com.market.TradeResult;
import com.trading.Order;
import com.trading.StockHolding;
import com.trading.User;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;

//@WebServlet("/api/orders/*")
public class OrderApiServlet extends HttpServlet {

    private UserDAO userDAO = new UserDAO();
    private OrderDAO orderDAO = new OrderDAO();
    private MarketPlace marketPlace = new MarketPlace();
    private StockHoldingDAO stockHoldingDAO = new StockHoldingDAO();

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse res) throws IOException {

        res.setContentType("application/json");

        String path = req.getPathInfo();
        String method = req.getMethod();
        if (path == null) path = "";

        HttpSession session = req.getSession(false);
        if (session == null) {
            res.getWriter().print(
                    new JSONObject().put("success", false).put("message", "Session expired")
            );
            return;
        }

        // get -> view my orders
        // post -> buy order, sell order
        // put -> modify order
        // delete -> cancel order
        if (path.equals("") && method.equals("GET")) {
            handleViewOrders(req, res);
        }
        else if (path.equals("/buy") && method.equals("POST")) {
            handleBuy(req, res);
        }
        else if (path.equals("/myStocks") && method.equals("GET")) {
            handleMyStocks(req, res);
        }
        else if (path.equals("/sell") && method.equals("POST")) {
            handleSell(req, res);
        }
        else if (method.equals("PUT") && isNumericPath(path)) {
            handleModify(req, res);
        }
        else if (method.equals("DELETE") && isNumericPath(path)) {
            handleDelete(req, res);
        }
        else {
            res.getWriter().print(new JSONObject().put("success", false).put("message", "Invalid API call"));
        }
    }

    private boolean isNumericPath(String path) {
        if (path == null || path.length() <= 1) return false;

        try {
            Integer.parseInt(path.substring(1)); // remove "/"
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private void handleViewOrders(HttpServletRequest req, HttpServletResponse res) throws IOException {

        String username = req.getSession().getAttribute("username").toString();
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

        res.getWriter().print(new JSONObject().put("success", true).put("data", arr));
    }

    // buy order
    private void handleBuy(HttpServletRequest req, HttpServletResponse res) throws IOException {

        String stock = req.getParameter("stockName");
        String qtyStr = req.getParameter("quantity");
        String priceStr = req.getParameter("price");

        if (stock == null || qtyStr == null || priceStr == null) {
            res.getWriter().print(
                    new JSONObject().put("success", false).put("message", "Missing parameters")
            );
            return;
        }

        int qty = Integer.parseInt(qtyStr);
        double price = Double.parseDouble(priceStr);

        User user = userDAO.findByUsername(req.getSession().getAttribute("username").toString());
        Order order = marketPlace.placeBuyOrder(user.getUserId(), stock.toUpperCase(), qty, price);

        if (order == null) {
            res.getWriter().print(
                    new JSONObject().put("success", false).put("message", "Insufficient balance")
            );
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

        JSONObject response = new JSONObject();
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

    // sell order
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

        List<StockHolding> holdings = stockHoldingDAO.findByDematId(user.getDematId());

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

    private void handleSell(HttpServletRequest req, HttpServletResponse res) throws IOException {

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

    // modify order
    private void handleModify(HttpServletRequest req, HttpServletResponse res) throws IOException {

        JSONObject response = new JSONObject();

        // get -> /api/orders/67
        int orderId;
        try {
            orderId = Integer.parseInt(req.getPathInfo().substring(1));
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Invalid order id");
            res.getWriter().print(response);
            return;
        }

        BufferedReader br = req.getReader();
        String body = br.readLine();   // quantity=5&price=25

        if (body == null || body.isEmpty()) {
            response.put("success", false);
            response.put("message", "Invalid input");
            res.getWriter().print(response);
            return;
        }

        String[] parts = body.split("&");
        int qty = Integer.parseInt(parts[0].split("=")[1]);
        double price = Double.parseDouble(parts[1].split("=")[1]);

        // auth check
        String username = req.getSession().getAttribute("username").toString();
        User user = userDAO.findByUsername(username);

        if (user == null) {
            response.put("success", false);
            response.put("message", "Unauthorized access");
            res.getWriter().print(response);
            return;
        }
        Order order = orderDAO.findById(orderId);
        if (order == null || order.getUserId() != user.getUserId()) {
            response.put("success", false);
            response.put("message", "Unauthorized access");
            res.getWriter().print(response);
            return;
        }

        boolean ok = marketPlace.modifyOrder(user.getUserId(), orderId, qty, price);
        response.put("success", ok);
        response.put("message", ok ? "Order modified" : "Cannot modify order");
        res.getWriter().print(response);
    }

    // cancel order
    private void handleDelete(HttpServletRequest req, HttpServletResponse res) throws IOException {

        JSONObject response = new JSONObject();

        int orderId;
        try {
            orderId = Integer.parseInt(req.getPathInfo().substring(1));
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Invalid order id");
            res.getWriter().print(response);
            return;
        }

        // authorization check
        String username = req.getSession().getAttribute("username").toString();
        User user = userDAO.findByUsername(username);
        if (user == null) {
            response.put("success", false);
            response.put("message", "Unauthorized access");
            res.getWriter().print(response);
            return;
        }
        Order order = orderDAO.findById(orderId);
        if (order == null || order.getUserId() != user.getUserId()) {
            response.put("success", false);
            response.put("message", "Unauthorized access");
            res.getWriter().print(response);
            return;
        }

        boolean ok = marketPlace.cancelOrder(user.getUserId(), orderId);
        response.put("success", ok);
        response.put("message", ok ? "Order cancelled" : "Cannot cancel order");
        res.getWriter().print(response);
    }
}