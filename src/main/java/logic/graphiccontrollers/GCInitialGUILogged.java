package logic.graphiccontrollers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.MenuItem;
import javafx.stage.Stage;
import java.io.IOException;
import logic.view.EssentialGUI;

public class GCInitialGUILogged extends EssentialGUI{
    @FXML
    private void handleLogout(ActionEvent event) {
        try {
            
            //altri comandi per il logout
            
            Parent root = FXMLLoader.load(getClass().getResource("/view/fxml/InitialGUI.fxml"));
            Stage stage = (Stage) ((MenuItem)event.getSource()).getParentPopup().getOwnerWindow();

            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
