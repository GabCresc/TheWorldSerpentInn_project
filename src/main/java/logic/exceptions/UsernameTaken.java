package logic.exceptions;

public class UsernameTaken extends Exception{
    private final String username;

    public UsernameTaken(String message, String username){
        super(message);
        this.username = username;
    }

    public String getUsername(){
        return this.username;
    }
}
