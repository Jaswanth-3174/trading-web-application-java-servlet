package api;

import com.dao.OrderDAO;
import com.dao.StockDAO;
import com.dao.UserDAO;
import com.trading.Order;
import com.trading.Stock;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;

//@WebServlet("/api/market/*")
public class MarketApiServlet extends HttpServlet {

    private StockDAO stockDAO = new StockDAO();
    private OrderDAO orderDAO = new OrderDAO();

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse res) throws IOException {

        res.setContentType("application/json");
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("username") == null) {
            res.getWriter().print(
                    new JSONObject().put("success", false).put("message", "Session expired")
            );
            return;
        }

        String path = req.getPathInfo();
        String method = req.getMethod();
        if (path == null) path = "";

        if ("/stocks".equals(path) && "GET".equals(method)) {
            handleViewStocks(res);
        } else if ("/orderbook".equals(path) && "POST".equals(method)) {
            handleOrderBook(req, res);
        }
        else {
            res.getWriter().print(new JSONObject().put("success", false).put("message", "Invalid API call"));
        }
    }


    private void handleViewStocks(HttpServletResponse res) throws IOException {

        JSONArray arr = new JSONArray();
        List<Stock> stocks = stockDAO.listAllStocks();

        for (Stock s : stocks) {
            JSONObject obj = new JSONObject();
            obj.put("name", s.getStockName());
            arr.put(obj);
        }

        res.getWriter().print(new JSONObject().put("success", true).put("data", arr));
    }

    private void handleOrderBook(HttpServletRequest req, HttpServletResponse res) throws IOException {

        JSONObject response = new JSONObject();

        BufferedReader br = req.getReader();
        String body = br.readLine();   // stock=TCS

        if (body == null || !body.startsWith("stock=")) {
            response.put("success", false);
            response.put("message", "Stock name required");
            res.getWriter().print(response);
            return;
        }

        String stockName = body.split("=")[1].toUpperCase();

        if (stockDAO.findByName(stockName) == null) {
            response.put("success", false);
            response.put("message", "Stock not found");
            res.getWriter().print(response);
            return;
        }

        int stockId = StockDAO.getStockIdByName(stockName);

        List<Order> buyOrders = orderDAO.getBuyOrders(stockId);
        List<Order> sellOrders = orderDAO.getSellOrders(stockId);

        JSONArray buyArr = new JSONArray();
        JSONArray sellArr = new JSONArray();

        for (Order o : buyOrders) {
            JSONObject obj = new JSONObject();
            obj.put("id", o.getOrderId());
            obj.put("user", UserDAO.findUsernameById(o.getUserId()));
            obj.put("qty", o.getQuantity());
            obj.put("price", o.getPrice());
            buyArr.put(obj);
        }

        for (Order o : sellOrders) {
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

        res.getWriter().print(response);
    }
}
