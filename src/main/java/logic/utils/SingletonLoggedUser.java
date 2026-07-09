package logic.utils;

import logic.utils.enums.UserTypes;

public class SingletonLoggedUser {
    private String username;
    private Integer userID;
    private UserTypes userType;
    private String email;
    private static SingletonLoggedUser instance = null;

    private SingletonLoggedUser() {}
    public static synchronized SingletonLoggedUser getInstance() {
        if (SingletonLoggedUser.instance == null) {
            SingletonLoggedUser.instance = new SingletonLoggedUser();
        }
        return SingletonLoggedUser.instance;
    }
    // Reset
    public void cleanSession() {
        this.username = null;
        this.userID = null;
        this.userType = null;
        this.email = null;
    }
    // Getter
    public String getUsername() {
        return username;
    }
    public int getUserID() {
        return (userID != null) ? userID : -1;
    }
    public UserTypes getUserType() {
        return userType;
    }
    public String getEmail(){
        return email;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public void setUserID(int userID) {
        this.userID = userID;
    }
    public void setUserType(UserTypes userType) {
        this.userType = userType;
    }
    public void setEmail(String email){
        this.email=email;
    }
}