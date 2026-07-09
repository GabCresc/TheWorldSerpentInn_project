package logic.graphiccontrollers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import logic.beans.BeanCampaign;
import logic.controllers.CampaignParticipationControl;

import java.util.logging.Level;
import java.util.logging.Logger;


public class GcCampaignCard {

    private static final Logger logger = Logger.getLogger(GcCampaignCard.class.getName());

    private BeanCampaign bean = new BeanCampaign();

    private CampaignParticipationControl participationControl;

    @FXML
    private Label nameLabel;

    @FXML
    private Button selectCampaignButton;

    @FXML
    private Label titleLabel;

    @FXML
    private AnchorPane pane;

    public void setData(BeanCampaign bean){
        this.bean = bean; //passo esattamente la campagna i: devo chiamare un'altra view con questi dati
        this.participationControl = new CampaignParticipationControl();
        String name = participationControl.getDmNameById(bean.getCampDMID());
        nameLabel.setText(name);
        titleLabel.setText(bean.getCampName());

    }

    @FXML
    public void selectCampaign(ActionEvent event){
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/campaignDetails.fxml"));
            Parent root  = loader.load();

            GcCampaignDetails controller = loader.getController();
            controller.setData(bean); //qui passo i dati relativi alla bean i per la prossima view

            Stage stage = (javafx.stage.Stage) titleLabel.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();

        }catch(Exception e){
            logger.log(Level.WARNING, "Can''t load FXML file: {0}", "/view/singleCampaign");
            logger.log(Level.WARNING, "Exception for loadPage", e);
        }
    }

}