package service;

import com.account.DematAccount;
import com.dao.DematAccountDAO;
import com.dao.UserDAO;
import com.trading.User;

import java.sql.SQLException;

public class AuthService {
    private UserDAO userDAO = new UserDAO();
    private DematAccountDAO dematAccountDAO = new DematAccountDAO();

    public boolean login(String userName, String password) throws SQLException {
        User user = userDAO.authenticateUser(userName, password);
        if(user == null) return false;
        return true;
    }

    public String signup(String userName, String password, String confirmPassword,
                          String panNumber, String dematPassword,
                          boolean isPromoter) throws SQLException{

        if(userDAO.isUsernameTaken(userName)){
            return "User name taken!";
        }
        if(!password.equals(confirmPassword)){
            return "Password doesn't match!";
        }
        DematAccount dematAccount = dematAccountDAO.findByPanNumber(panNumber);

        // new account creation
        if(dematAccount == null){
            DematAccount dematAccount1 = dematAccountDAO.createDematAccount(panNumber, password);
            userDAO.createUser(userName, password, dematAccount1.getDematAccountId(), isPromoter);
            return "Account created Successfully!";
        }

        // user has account already
        if(!dematAccount.getPassword().equals(dematPassword)){
            return "Incorrect Demat account password!";
        }

        if(userDAO.isActiveUserLinkedWithDematId(dematAccount.getDematAccountId())){
            return "Active user exists for the PAN number!";
        }

        userDAO.createUser(userName, password, dematAccount.getDematAccountId(), isPromoter);
        return "Account created Successfully!";
    }
}
