package com.worldserpentinn.model;

public class User {
    private String username;
    private String password;
    public User_Types UserType;
    public int user_ID;

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

    public int getUserID(){
        return user_ID;
    }
    
    public void setUserID(int user_ID){
        this.user_ID=user_ID;
    }

    public UserType getUserType(){
        return UserType;
    }
    
}
