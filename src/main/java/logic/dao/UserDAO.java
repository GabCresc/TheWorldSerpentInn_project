package logic.dao;

import logic.model.User;

import java.sql.PreparedStatement;

public interface UserDAO {
    User verifyLogin(String identifier, String password, boolean isGoogleLogin);
    User getLoggedUser(PreparedStatement pstatement);
    int getUserIDbyUsername(String username);
    String getUsernameByUserId(int userID);
    void registerUser(User user);
    boolean existenceUser(String username);
    User retrieveUserByUsername(String username);
    User retrieveUserByUserID(int userID);

}
