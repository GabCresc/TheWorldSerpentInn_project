package logic.graphiccontrollers;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import logic.beans.BeanUser;
import logic.controllers.LoginControl;
import javafx.scene.control.Label;
import logic.controllers.LoginGoogleControl;
import javafx.event.ActionEvent;
import logic.controllers.NotificationControl;
import logic.exceptions.InvalidValueException;
import logic.exceptions.TextTooLongException;
import logic.exceptions.TextTooShortException;
import logic.view.EssentialGUI;

import java.util.logging.Level;
import java.util.logging.Logger;


public class GcLogin {


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

    @FXML
    private Label title;

    private LoginControl standard;
    private LoginGoogleControl google;
    private static String FILL = "-fx-text-fill:red;";

    private final Logger logger = Logger.getLogger(GcLogin.class.getName());

    public GcLogin(){
        this.standard = new LoginControl();
        this.google = new LoginGoogleControl();
    }

    @FXML
    public void loginStandard(ActionEvent event) {
        String identifier = emailField.getText();
        String password = passwordField.getText();

        if (identifier.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Riempi tutti i campi!");
            return;
        }

        BeanUser bean = new BeanUser();

        try {
            bean.setUsername(identifier);
            bean.setPassword(password);
            BeanUser result = standard.verifyLogin(bean, false);
            if (result != null) {
                loadPage("/view/InitialGUILogged.fxml");
                EssentialGUI.showSuccessBanner("login");
            } else {
                logger.log(Level.INFO, "Credenziali errate");
                errorLabel.setStyle(FILL);
                errorLabel.setText("Password o username non validi!");
            }
        }catch(TextTooLongException | TextTooShortException | InvalidValueException e){
            //se entriamo in questo catch, sicuramente la password/l'username non è nel database
            logger.log(Level.INFO, "Login failed for formatting style error: {0}", e.getMessage());
            errorLabel.setStyle(FILL);
            errorLabel.setText("Password o username non validi!");
        }catch(Exception e){
            logger.log(Level.INFO, "System error while trying login", e);
            errorLabel.setStyle(FILL);
            errorLabel.setText("C'è stato un errore. Riprova più tardi");
        }
    }

    private void loadGoogleRegistrationPage(BeanUser bean) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/registrationgoogle.fxml"));
            Parent root  = loader.load();

            GcGoogleRegister controller = loader.getController();
            controller.initData(bean); //passiamo i dati della bean

            Stage stage = (javafx.stage.Stage) googleButton.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();

        } catch(Exception e) {
            logger.log(Level.WARNING, "Can't load Google Registration page", e);
        }
    }

    @FXML
    public void loginGoogle(ActionEvent event){
        googleButton.setDisable(true);
        logger.log(Level.INFO, "Starting Google authentication...");

        Task<String> googleAuthTask = new Task<>() { //thread che permette all'app di non andare in blocco mentre si fa il login con google,
            // eseguendo quello che c'è in {} dopo call()
            @Override
            protected String call() throws Exception{
                return google.startAuthGoogle();
            }
        };
        googleAuthTask.setOnSucceeded(w -> { //chiamato quando il task ha successo
            googleButton.setDisable(false); // disabilitiamo il pulsante googleButton
            String idToken = googleAuthTask.getValue();
            try {
                BeanUser bean = new BeanUser();
                bean.setIdToken(idToken);
                bean = standard.verifyLogin(bean, true);

                if (bean.getRegRequiredFlag()) { //controlliamo se è un nuovo utente
                    logger.log(Level.INFO, "New Google user! Going to registration");
                    // Richiama il metodo estratto qui:
                    loadGoogleRegistrationPage(bean);
                } else {
                    loadPage("/view/InitialGUILogged.fxml"); // Utente Google già esistente
                    EssentialGUI.showSuccessBanner("login");
                }
            } catch (Exception _) {
                logger.log(Level.SEVERE, "Exception occurred while trying authentication with Google");
            }
        });

        googleAuthTask.setOnFailed(w->{ //se il task fallisce
            googleButton.setDisable(false);
            errorLabel.setText("Autenticazione Google annullata");
            logger.log(Level.WARNING, "Google Auth tasked failed", googleAuthTask.getException());
        });

        // marchiamo il thread come daemon: termina dopo che tutti i thread dell'utente terminano
        Thread background = new Thread(googleAuthTask);
        background.setDaemon(true);
        background.start();

    }

    @FXML
    public void registerHere(ActionEvent event){
        logger.log(Level.INFO, "Going to registration page...");
        loadPage("/view/registration.fxml");
    }

    @FXML
    private void loadPage(String fxmlPath){
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root  = loader.load();

            Stage stage = (javafx.stage.Stage) emailField.getScene().getWindow();

            Scene scene = new javafx.scene.Scene(root);
            stage.setScene(scene);
            stage.show();

        }catch(Exception e){
            logger.log(Level.WARNING, "Can''t load FXML file: {0}", fxmlPath);
            logger.log(Level.WARNING, "Exception for loadPage", e);
        }
    }

}

