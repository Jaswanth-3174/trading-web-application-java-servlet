package controller;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import service.AuthService;

import java.sql.SQLException;

public class SignupServlet extends HttpServlet {

    AuthService authService = new AuthService();

    public void doPost(HttpServletRequest req, HttpServlet res){
        String username = req.getParameter("username");
        String password = req.getParameter("password");
        String confirmPassword = req.getParameter("confirm_password");
        String panNumber = req.getParameter("pan_number");
        String dematPassword = req.getParameter("demat_password");
        boolean isPromoter = (boolean) req.getParameter("is_promoter").equals("yes") ? true : false;

        try {
            String success = "Account created Successfully!";
            String result = authService.signup(username, password,
                    confirmPassword, panNumber, dematPassword, isPromoter);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}