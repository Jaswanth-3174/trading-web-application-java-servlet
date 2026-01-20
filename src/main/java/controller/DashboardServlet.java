package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

public class DashboardServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws IOException, ServletException {

        HttpSession session = req.getSession(false);

        if (session == null || session.getAttribute("username") == null) {
            // Not logged in
            res.sendRedirect("/MyServletApp_war_exploded/");
            return;
        }

        // Logged in
        res.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        res.setHeader("Pragma", "no-cache");
        res.setDateHeader("Expires", 0);

        // Forward (NOT redirect) to dashboard.html
        req.getRequestDispatcher("/dashboard/dashboard.html")
                .forward(req, res);
    }
}
