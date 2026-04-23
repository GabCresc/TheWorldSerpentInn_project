package logic.exceptions;

public class UsernameTaken extends Exception{
    // uso serializzazione?
    private String username;

    public UsernameTaken(){
        // empty
    }

    public UsernameTaken(String message, String username){
        super(message);
        this.username = username;
    }

    public String getUsername(){
        return this.username;
    }
}
