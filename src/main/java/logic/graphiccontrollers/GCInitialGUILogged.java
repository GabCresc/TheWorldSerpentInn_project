package logic.graphiccontrollers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import java.io.IOException;

import logic.controllers.LoginControl;
import logic.view.EssentialGUI;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GCInitialGUILogged extends EssentialGUI{

    private static final Logger logger = Logger.getLogger(GCInitialGUILogged.class.getName());
    @FXML
    private AnchorPane mainPane;

    @FXML
    public void handleLogout(ActionEvent event) {
        try {
            LoginControl loginControl = new LoginControl();
            loginControl.closeLoggedSession();

            Parent root = FXMLLoader.load(getClass().getResource("/view/InitialGUI.fxml"));
            Stage stage = (Stage) mainPane.getScene().getWindow();

            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Unable to load scene: login.fxml", e);
        }
    }
}