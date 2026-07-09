package logic.beans;

import logic.exceptions.TextTooShortException;
import logic.utils.enums.UserTypes;
import logic.exceptions.InvalidValueException;
import logic.exceptions.TextTooLongException;
import logic.model.User;

public class BeanUser {
    private String username;
    private String password;
    private UserTypes userType;
    private Integer userID;
    private String email;
    private String idToken;
    private Boolean regRequiredFlag; // flag per la view per segnalare la necessità di registrazione



    public BeanUser(User usrModel) { //serve nel controller per mostrare i dettagli della campagna
        this.userID = usrModel.getUserID();
        this.username = usrModel.getUsername();
        this.email = usrModel.getEmail();
        this.userType = usrModel.getUserType();
        this.password = usrModel.getPassword(); //risolve il problema dell'utente che fa login con Google e non ha bisogno della password
    }

    public BeanUser(){
        //empty
    }

    // Setters
    public void setRegRequiredFlag(boolean regRequiredFlag){
        this.regRequiredFlag = regRequiredFlag;
    }

    public void setIdToken(String idToken){
        this.idToken = idToken;
    }
    public void setUserType(UserTypes userType) { // mettere enum
        this.userType = userType;
    }

    public void setUsername(String username) throws InvalidValueException, TextTooLongException {
        if(username == null || username.equalsIgnoreCase("")) {
            throw new InvalidValueException("Per favore inserisce un username valido");
        }
        else if(username.length() > 25) {
            throw new TextTooLongException("Username troppo lungo: non eccedere i 25 caratteri");
        }
        this.username = username;
    }

    public void setPassword(String password) throws InvalidValueException, TextTooLongException, TextTooShortException {
        if(password == null || password.isEmpty()) {
            throw new InvalidValueException("Per favore inserire una password valida");
        }
        else if(password.length() > 25) {
            throw new TextTooLongException("Password troppo lunga: non eccedere i 25 caratteri");
        }else if(password.length() < 8){
            throw new TextTooShortException("Password troppo corta: superare gli 8 caratteri");
        }
        this.password = password;
    }

    public void setUserID(Integer userID) {
        this.userID = userID;
    }

    public void setEmail(String email){
        this.email = email;
    }

    //Getters

    public String getIdToken() {
        return this.idToken;
    }

    public UserTypes getUserType() {
        return this.userType;
    }

    public String getUsername() {
        return this.username;
    }

    public String getPassword() {
        return this.password;
    }

    public Integer getUserID() {
        return this.userID;
    }

    public String getEmail(){
        return this.email;
    }

    public Boolean getRegRequiredFlag(){
        return this.regRequiredFlag;
    }
}