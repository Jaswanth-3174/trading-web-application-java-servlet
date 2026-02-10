package api;

import com.account.TradingAccount;
import com.dao.*;
import com.trading.StockHolding;
import com.trading.User;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;

//@WebServlet("/api/account/*")
public class AccountApiServlet extends HttpServlet {

    private UserDAO userDAO = new UserDAO();
    private TradingAccountDAO tradingAccountDAO = new TradingAccountDAO();
    private StockHoldingDAO stockHoldingDAO = new StockHoldingDAO();

    protected void service(HttpServletRequest req, HttpServletResponse res) throws IOException {

        res.setContentType("application/json");

        String path = req.getPathInfo();
        String method = req.getMethod();
        if (path == null) path = "";

        HttpSession session = req.getSession(false);
        if (session == null) {
            res.getWriter().print(new JSONObject()
                    .put("success", false)
                    .put("message", "Session expired"));
            return;
        }

        if (path.equals("/balance") && method.equals("GET")) {
            handleViewBalance(req, res);
        }
        else if (path.equals("/balance") && method.equals("POST")) {
            handleAddBalance(req, res);
        }
        else if (path.equals("/portfolio") && method.equals("GET")) {
            handleViewPortfolio(req, res);
        }
        else if (path.equals("/delete") && method.equals("DELETE")) {
            handleDeleteAccount(req, res);
        }
        else {
            res.getWriter().print(new JSONObject()
                    .put("success", false)
                    .put("message", "Invalid API call"));
        }
    }

    // view balance
    private void handleViewBalance(HttpServletRequest req, HttpServletResponse res) throws IOException {

        User user = userDAO.findByUsername(req.getSession().getAttribute("username").toString());

        TradingAccount acc = tradingAccountDAO.findByUserId(user.getUserId());

        JSONObject data = new JSONObject();
        data.put("total", acc.getTotalBalance());
        data.put("available", acc.getAvailableBalance());
        data.put("reserved", acc.getReservedBalance());

        res.getWriter().print(new JSONObject()
                .put("success", true)
                .put("data", data));
    }

    // add balance
    private void handleAddBalance(HttpServletRequest req, HttpServletResponse res) throws IOException {

        JSONObject response = new JSONObject();

        BufferedReader br = req.getReader();
        String body = br.readLine(); // amount=500

        if (body == null || !body.startsWith("amount=")) {
            response.put("success", false).put("message", "Invalid amount");
            res.getWriter().print(response);
            return;
        }

        double amount = Double.parseDouble(body.split("=")[1]);
        if (amount <= 0) {
            response.put("success", false).put("message", "Amount must be > 0");
            res.getWriter().print(response);
            return;
        }

        User user = userDAO.findByUsername(req.getSession().getAttribute("username").toString());

        tradingAccountDAO.credit(user.getUserId(), amount);
        TradingAccount acc = tradingAccountDAO.findByUserId(user.getUserId());

        JSONObject bal = new JSONObject();
        bal.put("total", acc.getTotalBalance());
        bal.put("available", acc.getAvailableBalance());
        bal.put("reserved", acc.getReservedBalance());

        response.put("success", true);
        response.put("message", "Money added successfully");
        response.put("data", bal);

        res.getWriter().print(response);
    }

    private void handleViewPortfolio(HttpServletRequest req, HttpServletResponse res) throws IOException {

        User user = userDAO.findByUsername(req.getSession().getAttribute("username").toString());

        List<StockHolding> holdings = stockHoldingDAO.findByDematId(user.getDematId());
        JSONArray arr = new JSONArray();

        for (StockHolding s : holdings) {
            JSONObject obj = new JSONObject();
            obj.put("stockName", StockDAO.getStockNameById(s.getStockId()));
            obj.put("total", s.getTotalQuantity());
            obj.put("reserved", s.getReservedQuantity());
            obj.put("available", s.getAvailableQuantity());
            arr.put(obj);
        }

        res.getWriter().print(new JSONObject()
                .put("success", true)
                .put("data", arr));
    }

    private void handleDeleteAccount(HttpServletRequest req, HttpServletResponse res) throws IOException {

        JSONObject response = new JSONObject();

        BufferedReader br = req.getReader();
        String body = br.readLine(); // confirm=CONFIRM

        if (body == null || !body.equalsIgnoreCase("confirm=CONFIRM")) {
            response.put("success", false)
                    .put("message", "Type CONFIRM to delete account");
            res.getWriter().print(response);
            return;
        }

        User user = userDAO.findByUsername(req.getSession().getAttribute("username").toString());

        userDAO.deleteUser(user.getUserId());
        req.getSession().invalidate();

        response.put("success", true)
                .put("message", "Account deleted successfully");

        res.getWriter().print(response);
    }
}
