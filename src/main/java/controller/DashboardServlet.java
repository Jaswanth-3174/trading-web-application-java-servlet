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

public class DashboardServlet extends HttpServlet {

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

        req.getRequestDispatcher("/dashboard/dashboard.html").forward(req, res);
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
    }
}