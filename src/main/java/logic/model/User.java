package logic.model;
import logic.utils.enums.UserTypes;

public class User {
    private String username;
    private String password;
    private UserTypes type;
    private int userID;
    private String email;

    public User(){
        //empty
    }

    public User(String email){
        this.email = email;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public int getUserID() {
        return userID;
    }

    public String getEmail(){
        return email;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public UserTypes getUserType() {
        return type;
    }

    public void setUserType(UserTypes type) {
        this.type = type;

    }

    public void setEmail(String email){
        this.email = email;
    }
}
