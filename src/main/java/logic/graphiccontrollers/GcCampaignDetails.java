package logic.graphiccontrollers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.Pane;
import logic.beans.BeanCampaign;
import logic.beans.BeanUser;
import logic.controllers.CampaignParticipationControl;
import logic.exceptions.RequestAlreadySent;
import logic.utils.SingletonLoggedUser;
import logic.utils.enums.Mode;
import logic.view.EssentialGUI;

import java.util.logging.Level;
import java.util.logging.Logger;

public class GcCampaignDetails extends EssentialGUI {

    @FXML
    private Button confirmButton;

    @FXML
    private Label dmLabel;

    @FXML
    private Button goBackButton;

    @FXML
    private Label hourLabel;

    @FXML
    private MenuItem logoutItem;

    @FXML
    private Label numberOfPlayersLabel;

    @FXML
    private Label placeplatformLabel;

    @FXML
    private Label modeLabel;

    @FXML
    private Pane scalesBackground;

    @FXML
    private Label timeSessionLabel;

    @FXML
    private Label titleLabel;

    @FXML
    private Label errorLabel;

    private BeanCampaign beanCampaign;

    private static final Logger logger = Logger.getLogger(GcCampaignDetails.class.getName());

    private final CampaignParticipationControl participationControl = new CampaignParticipationControl();

    @FXML
    public void setData(BeanCampaign bean){ //usiamo i dati della bean
        this.beanCampaign = bean;
        this.titleLabel.setText(beanCampaign.getCampName());
        this.hourLabel.setText(beanCampaign.getCampFreq());
        String timeSession = beanCampaign.getCampTimeSession().toString();
        this.timeSessionLabel.setText(timeSession);
        participationControl.showCampaignDetails(beanCampaign);
        this.numberOfPlayersLabel.setText(beanCampaign.getAcceptedPlayers().size() + "/" + beanCampaign.getMaxNumberOfPlayers().toString());
        if(beanCampaign.getCampMode() == Mode.OFFLINE) {
            this.placeplatformLabel.setText(beanCampaign.getCampCity());
            this.modeLabel.setText(beanCampaign.getCampMode().toString());
        }else {
            this.placeplatformLabel.setText(beanCampaign.getPlatform());
            this.modeLabel.setText(beanCampaign.getCampMode().toString());
        }
        String name = participationControl.getDmNameById(bean.getCampDMID());
        this.dmLabel.setText(name);
    }

    @FXML
    public void confirmAction(ActionEvent event){ //confermiamo la partecipazione
        errorLabel.setText("");

        BeanUser beanUser = new BeanUser();
        beanUser.setUserID(SingletonLoggedUser.getInstance().getUserID());

        try {
            boolean success = participationControl.participate(beanCampaign, beanUser);

            if (success) {
                changeToConfirmationPage(event);
            } else {
                errorLabel.setText("Impossibile partecipare: la campagna è piena o già hai richiesto la partecipazione.");
            }
        }catch(RequestAlreadySent e){
            errorLabel.setText(e.getMessage());
            errorLabel.setStyle("-fx-text-fill: red;");
        }
    }

    @FXML
    public void goBackAction(ActionEvent event){
        try{
            changeGUI(event, "/view/viewCampaigns.fxml");
        }catch(Exception _){
            logger.log(Level.WARNING, "Exception occurred while loading campaign list");
            errorLabel.setText("Errore nel caricare la lista delle campagne disponibili");
        }
    }

    public void changeToConfirmationPage(ActionEvent event){
        try {
            changeGUI(event, "/view/confirmationPage.fxml");
        } catch (Exception e) {
            logger.log(Level.WARNING, "Exception occurred while loading confirmationPage", e);
            errorLabel.setText("Errore nel caricare la pagina di conferma");
        }
    }

}