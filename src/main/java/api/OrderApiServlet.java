package api;

import com.dao.OrderDAO;
import com.dao.StockDAO;
import com.dao.UserDAO;
import com.market.MarketPlace;
import com.market.TradeResult;
import com.trading.Order;
import com.trading.User;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

@WebServlet("/api/orders/*")
public class OrderApiServlet extends HttpServlet {

    private UserDAO userDAO = new UserDAO();
    private OrderDAO orderDAO = new OrderDAO();
    private MarketPlace marketPlace = new MarketPlace();

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
        else if (path.equals("/sell") && method.equals("POST")) {
            handleSell(req, res);
        }
        else if (method.equals("PUT")) {
            handleModify(req, res);
        }
        else if (method.equals("DELETE")) {
            handleDelete(req, res);
        }
        else {
            res.getWriter().print(
                    new JSONObject().put("success", false).put("message", "Invalid API call")
            );
        }
    }

    /* ---------- GET : VIEW MY ORDERS ---------- */
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

        res.getWriter().print(
                new JSONObject().put("success", true).put("data", arr)
        );
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

    /* ---------- POST : SELL ORDER ---------- */
    private void handleSell(HttpServletRequest req, HttpServletResponse res) throws IOException {

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

        User user = userDAO.findByUsername(
                req.getSession().getAttribute("username").toString()
        );

        Order order = marketPlace.placeSellOrder(
                user.getUserId(), stock.toUpperCase(), qty, price
        );

        if (order == null) {
            res.getWriter().print(
                    new JSONObject().put("success", false).put("message", "Not enough stocks")
            );
            return;
        }

        res.getWriter().print(
                new JSONObject().put("success", true).put("orderId", order.getOrderId())
        );
    }

    /* ---------- PUT : MODIFY ORDER ---------- */
    private void handleModify(HttpServletRequest req, HttpServletResponse res) throws IOException {

        int orderId = Integer.parseInt(req.getPathInfo().substring(1));
        int qty = Integer.parseInt(req.getParameter("quantity"));
        double price = Double.parseDouble(req.getParameter("price"));

        User user = userDAO.findByUsername(
                req.getSession().getAttribute("username").toString()
        );

        boolean ok = marketPlace.modifyOrder(user.getUserId(), orderId, qty, price);

        res.getWriter().print(
                new JSONObject().put("success", ok)
                        .put("message", ok ? "Order modified" : "Cannot modify order")
        );
    }

    /* ---------- DELETE : CANCEL ORDER ---------- */
    private void handleDelete(HttpServletRequest req, HttpServletResponse res) throws IOException {

        int orderId = Integer.parseInt(req.getPathInfo().substring(1));

        User user = userDAO.findByUsername(
                req.getSession().getAttribute("username").toString()
        );

        boolean ok = marketPlace.cancelOrder(user.getUserId(), orderId);

        res.getWriter().print(
                new JSONObject().put("success", ok)
                        .put("message", ok ? "Order cancelled" : "Cannot cancel order")
        );
    }
}
