package logic.graphiccontrollers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import logic.beans.BeanUser;
import logic.controllers.LoginControl;
import logic.exceptions.InvalidValueException;
import logic.exceptions.TextTooLongException;
import logic.exceptions.UsernameTaken;
import logic.utils.enums.UserTypes;

import java.util.logging.Level;
import java.util.logging.Logger;

public class CgRegister {

    private static final Logger logger = Logger.getLogger(CgRegister.class.getName());

    @FXML private TextField emailField;

    @FXML private TextField usernameField;

    @FXML private Label errorLabel;

    @FXML private Button gobackButton;

    @FXML private PasswordField passwordField;

    @FXML private PasswordField passwordField1;

    @FXML private Button registerButton;

    @FXML private ComboBox<String> roleMenu;

    @FXML private TextArea textfield;

    private LoginControl controller;

    public CgRegister(){
        this.controller = new LoginControl();
    }

    @FXML
    public void initialize(){
        roleMenu.getItems().addAll("Player", "DM");
        roleMenu.setValue("Player");
    }

    @FXML
    public void registerAction(ActionEvent event){
        String username = usernameField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();
        String password1 = passwordField1.getText();
        boolean result = checkPassword(password,password1);
        String role = roleMenu.getValue();

        if(username.isEmpty() || email.isEmpty() || password.isEmpty()){
            errorLabel.setText("Riempire tutti i campi!");
            return;
        }

        if(result == false){
            errorLabel.setText("Le password non coincidono");
            return;
        }
        BeanUser bean = new BeanUser();
        try{
            bean.setUsername(username);
        }catch(InvalidValueException | TextTooLongException e){
            errorLabel.setText("Inserire username/non superare i 30 caratteri");
            return;
        }

        bean.setEmail(email);
        bean.setPassword(password);
        if(UserTypes.PLAYER.toString().equalsIgnoreCase(role)){
            bean.setUserType(UserTypes.PLAYER);
        }else{
            bean.setUserType(UserTypes.DM);
        }

            try{
                controller.completeRegistration(bean);
                errorLabel.setText("Registrazione completata! Torna al login");
                loadPage("/view/login.fxml");

            }catch(UsernameTaken e){
                    errorLabel.setText("Username giù in uso!");
                    logger.log(Level.INFO, e.getMessage());
            }

    }

    public boolean checkPassword(String password, String password1){
        return password.equals(password1);
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
    public void goBackButton(ActionEvent event){

        loadPage("/view/login.fxml");
    }

}
