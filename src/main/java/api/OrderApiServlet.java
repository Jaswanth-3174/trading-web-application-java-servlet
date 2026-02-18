package api;

import com.dao.*;
import com.market.MarketPlace;
import com.market.TradeResult;
import com.trading.Order;
import com.trading.StockHolding;
import com.trading.User;
import jakarta.servlet.http.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

public class OrderApiServlet extends HttpServlet {

    private UserDAO userDAO = new UserDAO();
    private OrderDAO orderDAO = new OrderDAO();
    private MarketPlace marketPlace = new MarketPlace();
    private StockHoldingDAO stockHoldingDAO = new StockHoldingDAO();
    private TransactionDAO transactionDAO = new TransactionDAO();

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse res) throws IOException {

        res.setContentType("application/json");
        JSONObject response = new JSONObject();

        try {
            String path = req.getPathInfo();
            String method = req.getMethod();
            if (path == null) path = "";

            User user = getLoggedInUser(req);
            if (user == null) {
                res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.put("success", false);
                response.put("message", "Unauthorized access");
                res.getWriter().print(response);
                return;
            }

            if (path.equals("") && method.equals("GET")) {
                handleViewOrders(res, user);
            }
            else if (path.equals("/buy") && method.equals("POST")) {
                handleBuy(req, res, user);
            }
            else if (path.equals("/myStocks") && method.equals("GET")) {
                handleMyStocks(res, user);
            }
            else if (path.equals("/sell") && method.equals("POST")) {
                handleSell(req, res, user);
            }
            else if (method.equals("PUT") && isNumericPath(path)) {
                handleModify(req, res, user);
            }
            else if (method.equals("DELETE") && isNumericPath(path)) {
                handleDelete(req, res, user);
            }
            else {
                response.put("success", false);
                response.put("message", "Invalid API call");
                res.getWriter().print(response);
            }

        } catch (Exception e) {

            e.printStackTrace();

            res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.put("success", false);
            response.put("message", "Server error");
            res.getWriter().print(response);
        }
    }

    private User getLoggedInUser(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        if (session == null) return null;

        String username = (String) session.getAttribute("username");
        if (username == null) return null;

        return userDAO.findByUsername(username);
    }

    private boolean isNumericPath(String path) {
        try {
            Integer.parseInt(path.substring(1));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isUnauthorized(Order order, User user) {
        if(order == null) return true;
        if(order.getUserId() != user.getUserId()) return true;
        return false;
    }

    private void handleViewOrders(HttpServletResponse res, User user) throws IOException {

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

        res.getWriter().print(
                new JSONObject()
                        .put("success", true)
                        .put("data", arr)
        );
    }

    private void handleBuy(HttpServletRequest req,
                           HttpServletResponse res,
                           User user) throws IOException {

        JSONObject response = new JSONObject();

        try {
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

            if (quantity <= 0 || price <= 0) {
                response.put("success", false);
                response.put("message", "Invalid quantity or price");
                res.getWriter().print(response);
                return;
            }

            Order order = marketPlace.placeBuyOrder(user.getUserId(),
                    stockName.toUpperCase(), quantity, price);

            if (order == null) {
                response.put("success", false);
                response.put("message", "Insufficient balance");
                res.getWriter().print(response);
                return;
            }

            TradeResult trade = transactionDAO.getLastTrade(user.getUserId(), order.getOrderId());
            if (trade == null && order.getQuantity() == 0) {
                trade = transactionDAO.getLastTrade(
                        user.getUserId(),
                        order.getStockId()
                );
            }

            int remaining = order.getQuantity();
            String status;

            if (remaining == 0) {
                status = "FILLED";
            }
            else if (trade != null) {
                status = "PARTIALLY_FILLED";
            }
            else {
                status = "WAITING";
            }

            response.put("success", true);
            response.put("orderId", order.getOrderId());
            response.put("status", status);
            response.put("remaining", remaining);

            if (trade != null) {

                JSONObject tradeJson = new JSONObject();
                tradeJson.put("buyer", trade.getBuyer());
                tradeJson.put("seller", trade.getSeller());
                tradeJson.put("stock", trade.getStock());
                tradeJson.put("quantity", trade.getQuantity());
                tradeJson.put("price", trade.getPrice());
                tradeJson.put("total", trade.getTotal());

                response.put("trade", tradeJson);
            }

            res.getWriter().print(response);

        } catch (Exception e) {

            response.put("success", false);
            response.put("message", "Server error while placing order");
            res.getWriter().print(response);
        }
    }

    private void handleMyStocks(HttpServletResponse res,
                                User user) throws IOException {

        List<StockHolding> holdings = stockHoldingDAO.findByDematId(user.getDematId());

        JSONArray arr = new JSONArray();

        for (StockHolding h : holdings) {
            if (h.getAvailableQuantity() > 0) {
                JSONObject obj = new JSONObject();
                obj.put("name", h.getStockName());
                obj.put("qty", h.getAvailableQuantity());
                arr.put(obj);
            }
        }

        res.getWriter().print(
                new JSONObject()
                        .put("success", true)
                        .put("data", arr)
        );
    }

    private void handleSell(HttpServletRequest req,
                            HttpServletResponse res,
                            User user) throws IOException {

        String stockName = req.getParameter("stockName");
        String qtyStr = req.getParameter("quantity");
        String priceStr = req.getParameter("price");

        if (stockName == null || qtyStr == null || priceStr == null) {
            res.getWriter().print(
                    new JSONObject().put("success", false)
                            .put("message", "Missing parameters")
            );
            return;
        }

        int quantity = Integer.parseInt(qtyStr);
        double price = Double.parseDouble(priceStr);

        if (quantity <= 0 || price <= 0) {
            res.getWriter().print(
                    new JSONObject().put("success", false)
                            .put("message", "Invalid quantity or price")
            );
            return;
        }

        Order order = marketPlace.placeSellOrder(user.getUserId(),
                stockName.toUpperCase(), quantity, price);

        if (order == null) {
            res.getWriter().print(
                    new JSONObject().put("success", false)
                            .put("message", "Not enough stocks available")
            );
            return;
        }

        res.getWriter().print(
                new JSONObject()
                        .put("success", true)
                        .put("orderId", order.getOrderId())
                        .put("remaining", order.getQuantity())
        );
    }

    private void handleModify(HttpServletRequest req,
                              HttpServletResponse res,
                              User user) throws IOException {

        JSONObject response = new JSONObject();

        try {

            int orderId = Integer.parseInt(req.getPathInfo().substring(1));
            Order order = orderDAO.findById(orderId);

            if (isUnauthorized(order, user)) {
                res.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.put("success", false);
                response.put("message", "Unauthorized access");
                res.getWriter().print(response);
                return;
            }

            String body = req.getReader().readLine();  // quantity=5&price=100

            if (body == null || body.isEmpty()) {
                response.put("success", false);
                response.put("message", "Invalid input");
                res.getWriter().print(response);
                return;
            }

            String[] parts = body.split("&");
            int quantity = Integer.parseInt(parts[0].split("=")[1]);
            double price = Double.parseDouble(parts[1].split("=")[1]);

            if(order.getQuantity() == quantity && order.getPrice() == price){
                response.put("success", false);
                response.put("message", "Change the price or quantity to modify!");
                res.getWriter().print(response);
                return;
            }

            if (quantity <= 0 || price <= 0) {
                response.put("success", false);
                response.put("message", "Invalid quantity or price");
                res.getWriter().print(response);
                return;
            }

            boolean ok = marketPlace.modifyOrder(user.getUserId(), orderId, quantity, price);

            response.put("success", ok);
            response.put("message", ok ? "Order modified" : "Modification failed");

            res.getWriter().print(response);

        } catch (Exception e) {
            e.printStackTrace();

            response.put("success", false);
            response.put("message", "Server error");
            res.getWriter().print(response);
        }
    }

    private void handleDelete(HttpServletRequest req,
                              HttpServletResponse res,
                              User user) throws IOException {

        int orderId = Integer.parseInt(req.getPathInfo().substring(1));
        Order order = orderDAO.findById(orderId);

        if (isUnauthorized(order, user)) {
            res.setStatus(HttpServletResponse.SC_FORBIDDEN);
            res.getWriter().print(
                    new JSONObject().put("success", false)
                            .put("message", "Unauthorized access")
            );
            return;
        }

        boolean ok = marketPlace.cancelOrder(user.getUserId(), orderId);

        res.getWriter().print(
                new JSONObject()
                        .put("success", ok)
                        .put("message",
                                ok ? "Order cancelled" : "Cancel failed")
        );
    }
}