package logic.controllers;

import logic.beans.BeanUser;
import logic.controllers.abstract_factory_dao.DaoFactory;
import logic.dao.UserDAO;
import logic.exceptions.UsernameTaken;
import logic.model.ModelCampaign;

import logic.model.User;
import logic.utils.SingletonLoggedUser;

import java.util.logging.Level;
import java.util.logging.Logger;

public class LoginControl {

    private static final Logger logger = Logger.getLogger(LoginControl.class.getName());

    private UserDAO daoUser;
    private LoginGoogleControl googleController;

    public LoginControl() {
        this.daoUser = DaoFactory.getFactory().createUserDAO();
        this.googleController = new LoginGoogleControl();
    }


    public LoginControl(UserDAO userDAO){ //utile per il testing
        this.daoUser = userDAO;
        this.googleController = new LoginGoogleControl();
    }

    public BeanUser verifyLogin(BeanUser userLogin, boolean isGoogle){
        //login con google
        if(isGoogle){
            return googleController.loginWithGoogle(userLogin.getIdToken());
        }else{
            return loginStandard(userLogin.getUsername(), userLogin.getPassword());
        }
    }

    // Collections.singletonList restituisce una lista immutabile, serializzabile, che contiene solo l'oggetto indicato
    // vs Array.asList che è modificabile

    public boolean isUsernameTaken(String username){

        User user = daoUser.retrieveUserByUsername(username);
        if (user == null) {
            return false;
        }
        return(user.getUsername().equals(username));
    }

    public boolean completeRegistration(BeanUser bean) throws UsernameTaken {
        User newUser = new User();
        newUser.setUsername(bean.getUsername());
        newUser.setEmail(bean.getEmail());
        newUser.setUserType(bean.getUserType());
        newUser.setPassword(bean.getPassword());

        if(isUsernameTaken(bean.getUsername())){
            throw new UsernameTaken("Questo username è già in utilizzo", bean.getUsername());
        }

        daoUser.registerUser(newUser);
        User registeredUser = daoUser.retrieveUserByUsername(newUser.getUsername());
        if (registeredUser != null) {
            initLoggedSession(registeredUser);
        }

        return true;
    }

    public BeanUser loginStandard(String username, String password){
        User user = daoUser.verifyLogin(username, password, false);
        if(user != null){
            initLoggedSession(user);
            return new BeanUser(user);
        }else{
            logger.log(Level.INFO, "An error has occurred");
            return null; // Password errata, errore generico
        }
    }

    private void initLoggedSession(User user){
        SingletonLoggedUser session = SingletonLoggedUser.getInstance();
        session.setUserID(user.getUserID());
        session.setUsername(user.getUsername());
        session.setEmail(user.getEmail());
        session.setUserType(user.getUserType());
        logger.log(Level.INFO, "Session created for user: {0}", user.getUsername());
    }

    public void closeLoggedSession() {
        SingletonLoggedUser.getInstance().cleanSession();
        logger.log(Level.INFO, "Session closed successfully");
    }

}