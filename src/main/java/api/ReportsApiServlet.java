package api;

import com.dao.StockDAO;
import com.dao.TransactionDAO;
import com.dao.UserDAO;
import com.trading.Transaction;
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

//@WebServlet("/api/transactions/*")
public class ReportsApiServlet extends HttpServlet {

    private UserDAO userDAO = new UserDAO();
    private TransactionDAO transactionDAO = new TransactionDAO();

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

        if(path == null) path = "";

        if (path.equals("") && method.equals("GET")) {
            handleViewMyTransactions(req, res);
        }
        else if (path.equals("/all") && method.equals("GET")) {
            handleViewAllTransactions(req, res);
        }
        else {
            res.getWriter().print(
                    new JSONObject().put("success", false).put("message", "Invalid API call")
            );
        }
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
}
