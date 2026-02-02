package controller;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import service.AuthService;

import java.io.IOException;

public class AuthServlet extends HttpServlet {

    private AuthService authService = new AuthService();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res)  throws IOException {
        String action = req.getParameter("action");
        System.out.println("ACTION RECEIVED = [" + action + "]");
        if("login".equals(action)){
            login(req, res);
        }else if("signup".equals(action)){
            signup(req, res);
        }else if("logout".equals(action)){
            logout(req, res);
        }else{
            res.setStatus(400);
            res.getWriter().write("Invalid action");
        }
    }

    private void login(HttpServletRequest req, HttpServletResponse res) throws IOException {
        String userName = req.getParameter("username");
        String password = req.getParameter("password");

        String result = authService.login(userName, password);

        if(result.equals("Success")){
            HttpSession session = req.getSession();
            session.setAttribute("username", userName);
            res.setStatus(200);
        } else {
            res.setStatus(401);
            res.getWriter().write(result);
        }

    }

    private void signup(HttpServletRequest req, HttpServletResponse res) throws IOException{
        String username = req.getParameter("username");
        String password = req.getParameter("password");
        String confirmPassword = req.getParameter("confirm_password");
        String panNumber = req.getParameter("pan_number");
        String dematPassword = req.getParameter("demat_password");
        boolean isPromoter = "yes".equals(req.getParameter("is_promoter"));

        String result = authService.signup(username, password,
                    confirmPassword, panNumber, dematPassword, isPromoter);

        if(result.equals("Success")){
            HttpSession session = req.getSession();
            session.setAttribute("username", username);
            res.setStatus(200);
        } else {
            res.setStatus(401);
            res.getWriter().write(result);
        }
    }

    private void logout(HttpServletRequest req, HttpServletResponse res) throws IOException {
        res.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        res.setHeader("Pragma", "no-cache");
        res.setDateHeader("Expires", 0);

        HttpSession session = req.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        res.getWriter().write("Logged out");
    }
}
