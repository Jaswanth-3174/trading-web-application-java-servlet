package api;

import com.account.TradingAccount;
import com.dao.TradingAccountDAO;
import com.dao.UserDAO;
import com.trading.User;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.json.JSONObject;

import java.io.IOException;

@WebServlet("/api/account/*")
public class ApiServlet extends HttpServlet {

    private UserDAO userDAO = new UserDAO();
    private TradingAccountDAO tradingAccountDAO = new TradingAccountDAO();

    protected void service(HttpServletRequest req, HttpServletResponse res) throws IOException {
        res.setContentType("application/json");

        String path = req.getPathInfo();
        String method = req.getMethod();

        if(path == null) path = "";

        switch (path){
            case "/balance":
                if(method.equals("GET")){
                    handleBalance(req, res);
                }
                break;

            default:

        }
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
}
