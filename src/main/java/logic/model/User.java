package logic.model;

import logic.utils.enums.UserTypes;


public class Model_User {
    //dummy
    private String username;
    private String password;
    private Integer Id;
    private UserTypes usertype;

    public void setID(int Id){
        this.Id = Id;
    }

    public void setUsername(String username){
        this.username = username;
    }

    public void setUserType(UserTypes usertype){
        this.usertype = usertype;
    }
    public String getUsername(){
        return this.username;
    }

    public String getPassword(){
        return this.password;
    }

    public UserTypes getUsertype(){
        return this.usertype;
    }
}
