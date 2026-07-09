package logic.graphiccontrollers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import logic.view.EssentialGUI;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.IOException;

public class GCInitialGUI extends EssentialGUI{
    private static final Logger logger = Logger.getLogger(GCInitialGUI.class.getName());

    @FXML
    private void login(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/view/login.fxml"));
            Stage stage = (Stage) ((javafx.scene.Node)event.getSource()).getScene().getWindow();

            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Unable to load scene: login.fxml", e);
        }
    }
}