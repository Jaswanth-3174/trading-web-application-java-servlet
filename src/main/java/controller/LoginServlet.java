package controller;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import service.AuthService;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

public class LoginServlet extends HttpServlet {

    AuthService authService = new AuthService();

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
        String userName = req.getParameter("username");
        String password = req.getParameter("password");
        try{
            if(!authService.login(userName, password)){
                res.sendRedirect("auth/login.html?msg=invalid");
                return;
            }
            HttpSession session = req.getSession();
            session.setAttribute("username", userName);
            res.sendRedirect("dashboard/dashboard.html");
        } catch (Exception e) {
            res.sendRedirect("auth/login.html?msg=error");
        }
    }
}
