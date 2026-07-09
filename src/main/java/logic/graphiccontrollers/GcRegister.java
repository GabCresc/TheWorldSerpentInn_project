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
import logic.exceptions.TextTooShortException;
import logic.exceptions.UsernameTaken;
import logic.utils.enums.UserTypes;

import java.util.logging.Level;
import java.util.logging.Logger;

public class GcRegister {

    private static final Logger logger = Logger.getLogger(GcRegister.class.getName());

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

    public GcRegister(){
        this.controller = new LoginControl();
    }

    @FXML
    public void initialize(){ //inizializiamo il menu
        roleMenu.getItems().addAll("Player", "DM");
        roleMenu.setValue("Player");
    }

    @FXML
    public void registerAction(ActionEvent event) {
        //prendiamo i dati immessi dall'utente
        String identifier = usernameField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();
        String password1 = passwordField1.getText();
        boolean result = checkPassword(password,password1);
        String role = roleMenu.getValue();

        if(identifier.isEmpty() || email.isEmpty() || password.isEmpty()){
            errorLabel.setText("Riempire tutti i campi!");
            return;
        }

        if(!result){
            errorLabel.setText("Le password non coincidono");
            return;
        }
        //popoliamo la bean
        BeanUser bean = new BeanUser();
        bean.setEmail(email);
        try {
            bean.setUsername(identifier);
            bean.setPassword(password);
        }catch(InvalidValueException | TextTooShortException | TextTooLongException e){
            errorLabel.setText(e.getMessage());
            errorLabel.setStyle("-fx-text-fill:red;");
            logger.log(Level.WARNING, "Error in registration: {0}", e.getMessage());
            return;
        }catch(Exception e){
            errorLabel.setText("Si è verificato un errore imprevisto");
            logger.log(Level.SEVERE, "Exception occurred while carrying on with registration", e);
            return;
        }
        if(UserTypes.PLAYER.toString().equalsIgnoreCase(role)){
            bean.setUserType(UserTypes.PLAYER);
        }else{
            bean.setUserType(UserTypes.DM);
        }

        try{
            controller.completeRegistration(bean); //completiamo la registrazione passando la bean
            errorLabel.setText("Registrazione completata! Torna al login");
            loadPage("/view/login.fxml");

        }catch(UsernameTaken e){
            errorLabel.setText(e.getMessage());
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
            logger.log(Level.WARNING, "Can''t load FXML file: {0}. Reason: {1}", new Object[]{fxmlPath, e.getMessage()});
        }
    }

    @FXML
    public void goBackButton(ActionEvent event){

        loadPage("/view/login.fxml");
    }

}