package service;

import com.account.DematAccount;
import com.dao.DematAccountDAO;
import com.dao.TradingAccountDAO;
import com.dao.UserDAO;
import com.trading.User;

public class AuthService {
    private UserDAO userDAO = new UserDAO();
    private DematAccountDAO dematAccountDAO = new DematAccountDAO();
    private TradingAccountDAO tradingAccountDAO = new TradingAccountDAO();

    public String login(String userName, String password){
        User user = userDAO.authenticateUser(userName, password);
        if(user == null) return "Invalid username or password";
        return "Success";
    }

    public String signup(String username, String userPassword, String confirmPassword,
                         String panNumber, String dematPassword, boolean isPromoter){

        if(userDAO.isUsernameTaken(username)){
            return "Username already taken!";
        }

        if(!userPassword.equals(confirmPassword)){
            return "Passwords do not match!";
        }

        DematAccount demat = dematAccountDAO.findByPanNumber(panNumber);

        // case 1 : demat exists
        if(demat != null){
            if(!dematAccountDAO.authenticate(panNumber, dematPassword)){
                return "Invalid Demat password!";
            }
            if(userDAO.isActiveUserLinkedWithDematId(demat.getDematAccountId())){
                return "This Demat account already has an active user!";
            }
        }
        // case 2 : create new demat
        else{
            demat = dematAccountDAO.createDematAccount(panNumber, dematPassword);
            if(demat == null){
                return "Failed to create Demat account!";
            }
        }

        User user = userDAO.createUser(username, userPassword, demat.getDematAccountId(),
                isPromoter);

        if(user == null){
            return "User creation failed!";
        }

        double initialBalance = 1000 + Math.random() * 4000;
        tradingAccountDAO.createTradingAccount(user.getUserId(), initialBalance);
        return "Success";
    }
}
