package logic.graphiccontrollers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import logic.beans.BeanUser;
import logic.controllers.LoginControl;
import javafx.scene.control.Label;
import logic.controllers.LoginGoogleControl;
import logic.utils.enums.UserTypes;
import javafx.event.ActionEvent;

import java.util.logging.Level;
import java.util.logging.Logger;


public class CgLogin {

    //eventualmente creare un'eccezione Login per fare in modo che, se il database non funziona o altro, l'utente riceva un msg chiaro

    @FXML
    private TextField emailField;

    @FXML
    private Hyperlink forgottenPasswordLink; // non implementato

    @FXML
    private Button googleButton;

    @FXML
    private Button loginButton;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Hyperlink registerLink;

    @FXML
    Label errorLabel;

    private LoginControl standard;
    private LoginGoogleControl google;

    private final Logger logger = Logger.getLogger(CgLogin.class.getName());

    public CgLogin(){
        this.standard = new LoginControl();
        this.google = new LoginGoogleControl();
    }

    @FXML
    private void loadPage(String fxmlPath){
        try{
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource(fxmlPath));
            javafx.scene.Parent root  = loader.load();

            javafx.stage.Stage stage = (javafx.stage.Stage) emailField.getScene().getWindow();

            javafx.scene.Scene scene = new javafx.scene.Scene(root);
            stage.setScene(scene);
            stage.show();

        }catch(Exception e){
            logger.log(Level.WARNING, "Can't load FXML file: " + fxmlPath, e.getMessage());
        }
    }

    @FXML
    public void loginStandard(ActionEvent event){
        String identifier = emailField.getText();
        String password = passwordField.getText();


        BeanUser bean = new BeanUser();
        try {
            bean.setUsername(identifier);
            bean.setPassword(password);
        }catch(Exception e){
            logger.log(Level.WARNING, "");
        }

        if(identifier.isEmpty() || password.isEmpty()){
            errorLabel.setText("Riempi tutti i campi!");
            return;
        }
        try{
            BeanUser result = standard.verifyLogin(bean, false);
            System.out.println(result.getPassword());
            System.out.println(result.getUsername());
            if(result != null){
                if(result.getRegRequiredFlag()){
                    logger.log(Level.INFO, "L'utente non esiste. Passiamo alla registrazione");
                    //loadPage(Registration)
                }else {
                    decideRole(result);
                }
            }else{
                logger.log(Level.INFO, "Credenziali errate");
            }
        }catch(Exception e){
            logger.log(Level.INFO, "Password/username errato. Per favore riprovare", e);
        }
    }

    @FXML
    public void loginGoogle(ActionEvent event){
            googleButton.setDisable(true);
            logger.log(Level.INFO, "Starting Google authentication...");
            try {
                BeanUser bean = new BeanUser();
                String idToken = google.startAuthGoogle();
                System.out.println(idToken);
                bean.setIdToken(idToken);
                bean = standard.verifyLogin(bean, true);
                if(bean != null) {
                    if (UserTypes.DM.equals(bean.getUserType())) {
                        //loadPage(Homepage DM)
                    } else {
                        //loadPage(Homepage player)
                    }
                }else{
                    logger.log(Level.INFO, "Something went wrong with Google login");
                }
            }catch(Exception e){
                logger.log(Level.SEVERE, "Exception occurred while trying authentication with Google", e.getMessage());
            }

    }

    @FXML
    public void registerHere(ActionEvent event){
        logger.log(Level.INFO, "Going to registration page...");
        loadPage("/view/registration.fxml");
    }

    private void decideRole(BeanUser result){
        if (UserTypes.DM.equals(result.getUserType())) {
            logger.log(Level.INFO, "Login effettuato! Benvenuto " + result.getUsername());
            //loadPage(HomepageDM)
            //carica la homepage con login effettuato (homepageDM o homepagePlayer)
        } else {
            logger.log(Level.INFO, "Login effettuato! Benvenuto " + result.getUsername());
            //loadPage(HomepagePlayer)
            //carica la homepage player
        }
    }

}
