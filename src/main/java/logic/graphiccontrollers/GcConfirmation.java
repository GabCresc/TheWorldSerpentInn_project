package logic.graphiccontrollers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import logic.view.EssentialGUI;

import java.util.logging.Level;
import java.util.logging.Logger;

public class GcConfirmation extends EssentialGUI {

    @FXML
    private Button goBackButton;

    @FXML
    private MenuItem logoutItem;

    @FXML
    private Pane scalesBackground;

    @FXML
    private Hyperlink showCampaigns;

    @FXML
    private Label errorLabel;

    private static final Logger logger = Logger.getLogger(GcConfirmation.class.getName());


    @FXML
    public void goBackAction(ActionEvent event){
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/InitialGUILogged.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) scalesBackground.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        }catch(Exception _){
            logger.log(Level.WARNING, "Exception occurred while loading homepage");
            errorLabel.setText("Errore nel caricare homepage");
        }
    }

}