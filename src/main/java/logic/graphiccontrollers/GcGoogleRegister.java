package logic.graphiccontrollers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import logic.beans.BeanUser;
import logic.controllers.LoginGoogleControl;
import logic.utils.enums.UserTypes;

import java.util.logging.Level;
import java.util.logging.Logger;

public class GcRegisterGoogle {

    @FXML
    private Button gobackButton;

    @FXML
    private Button registerButton;

    @FXML
    private ComboBox<String> roleMenu;

    @FXML
    private TextArea textfield;

    @FXML
    private Label label1;

    private static final Logger logger = Logger.getLogger(GcRegisterGoogle.class.getName());

    private LoginGoogleControl googleControl;
    private BeanUser beanUser;

    public void GgRegisterGoogle(){
        this.googleControl = new LoginGoogleControl();
    }

    @FXML
    public void initializeMenu(){
        roleMenu.getItems().addAll("Player", "DM");
        roleMenu.setValue("Player");
    }

    public void initData(BeanUser bean){
        this.beanUser = bean;
        label1.setText("Benvenuto! Completa la registrazione");
    }

    @FXML
    void goBackButton2(ActionEvent event) {
            loadPage("/view/login.fxml");
    }

    @FXML
    void registerWithGoogle(ActionEvent event) {
        String role = roleMenu.getValue();

        if(role.equalsIgnoreCase(UserTypes.PLAYER.toString())){
            beanUser.setUserType(UserTypes.PLAYER);
        }else{
            beanUser.setUserType(UserTypes.DM);
        }

        try{
            googleControl.completeRegistrationGoogle(beanUser);
            loadPage("/view/registrationgoogle.fxml");
        }catch(Exception e){
            logger.log(Level.WARNING, "Error while updating role", e);
        }

    }

    @FXML
    private void loadPage(String fxmlPath){
        try{
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource(fxmlPath));
            javafx.scene.Parent root  = loader.load();

            javafx.stage.Stage stage = (javafx.stage.Stage) textfield.getScene().getWindow();

            javafx.scene.Scene scene = new javafx.scene.Scene(root);
            stage.setScene(scene);
            stage.show();

        }catch(Exception e){
            logger.log(Level.WARNING, "Can't load FXML file: " + fxmlPath, e.getMessage());
        }
    }

}

