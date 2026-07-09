package logic.graphiccontrollers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.scene.Node;

import java.util.List;
import java.util.ArrayList;
import java.io.IOException;

import logic.beans.BeanCampaign;
import logic.beans.BeanUser;
import logic.controllers.CreateCampaignControl;
import logic.view.EssentialGUI;

public class GCInvitePlayers extends EssentialGUI {

    private BeanCampaign campaignCreating;

    @FXML
    private TextField invitedPlayer;

    private List<BeanUser> invitingPlayers = new ArrayList<>();

    @FXML
    private Label playerInviteResult;

    CreateCampaignControl campaignController = new CreateCampaignControl();

    @FXML
    public void confirmInvite(ActionEvent event) {
        String selectedPlayer = invitedPlayer.getText();

        if (selectedPlayer == null || selectedPlayer.trim().isEmpty()) {
            playerInviteResult.setTextFill(Color.RED);
            playerInviteResult.setText("Inserisci un nome giocatore!");
            return;
        }

        int initialSize = invitingPlayers.size();

        invitingPlayers = campaignController.addNotifiedPlayer(invitingPlayers, selectedPlayer);

        if (invitingPlayers.size() > initialSize) {
            playerInviteResult.setTextFill(Color.GREEN);
            playerInviteResult.setText("Aggiunto alla lista dei giocatori da invitare");
        } else {
            playerInviteResult.setTextFill(Color.RED);
            playerInviteResult.setText("L'utente non esiste o non è un player");
        }

        invitedPlayer.setText("");
    }

    @FXML
    public void createCampaign(ActionEvent event) throws IOException {

        boolean success = campaignController.createCampaign(campaignCreating);

        if (success) {
            Integer campaignID = campaignCreating.getCampId();

            campaignController.notifyCreation(campaignID, invitingPlayers);

            Parent root = FXMLLoader.load(getClass().getResource("/view/InitialGUILogged.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } else {
            playerInviteResult.setTextFill(Color.RED);
            playerInviteResult.setText("Errore di sistema nella creazione della campagna.");
        }
    }

    public void receiveCampaign(BeanCampaign incomingCampaign) {
        this.campaignCreating = incomingCampaign;
    }
}
