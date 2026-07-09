package logic.graphiccontrollers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import logic.exceptions.InvalidValueException;
import logic.exceptions.TextTooLongException;
import logic.utils.SingletonLoggedUser;
import logic.utils.enums.Mode;
import logic.view.EssentialGUI;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GCCreateCampaign extends EssentialGUI {
    @FXML
    private TextField campaignNameTextField;
    @FXML
    private Label campaignNameError;
    @FXML
    private ChoiceBox<String> choseType;
    private static final String TYPE_OFFLINE = "offline";
    private static final String TYPE_ONLINE = "online";
    private String[] types = {TYPE_OFFLINE, TYPE_ONLINE};

    @FXML
    private Label typeError;
    @FXML
    private TextField insertLocation;
    @FXML
    private Label locationError;
    @FXML
    DatePicker startDatePicker;
    @FXML
    private Spinner<Integer> hourSpinner;
    @FXML
    private Spinner<Integer> minuteSpinner;
    @FXML
    private Label timeDateError;
    @FXML
    private ChoiceBox<String> choseFreq;
    private String[] freqTypes={"settimanale", "bisettimanale", "mensile"};
    @FXML
    private Label freqError;
    @FXML
    private Spinner<Integer> maxPlayersSpinner;
    @FXML
    private Label maxPlayersError;

    private static final Logger logger = Logger.getLogger(GCCreateCampaign.class.getName());
    private static final SingletonLoggedUser loggedUser = SingletonLoggedUser.getInstance();

    public void initialize(){

        choseType.getItems().addAll(types);
        choseFreq.getItems().addAll(freqTypes);

        SpinnerValueFactory<Integer> hourFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23, 12);
        hourSpinner.setValueFactory(hourFactory);

        SpinnerValueFactory<Integer> minuteFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, 0);
        minuteSpinner.setValueFactory(minuteFactory);

        SpinnerValueFactory<Integer> maxPlayersFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 10, 1);
        maxPlayersSpinner.setValueFactory(maxPlayersFactory);

        startDatePicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);

                LocalDate today = LocalDate.now();

                if (empty || date.isBefore(today)) {
                    setDisable(true);
                    setStyle("-fx-background-color: #e0e0e0; -fx-text-fill: #999999;");
                } else {
                    setDisable(false);
                    setStyle("");
                }
            }
        });
    }

    @FXML
    public void confirmCampaignCreation(ActionEvent event) throws IOException, InvalidValueException, TextTooLongException {
        // 1. Se la validazione fallisce, interrompiamo l'esecuzione
        if (!isFormValid()) {
            return;
        }

        // 2. Creiamo l'oggetto con i dati
        logic.beans.BeanCampaign campaignData = buildCampaignData(loggedUser.getUserID());

        // 3. Cambiamo schermata
        loadNextScene(campaignData, event);
    }

    private boolean isFormValid() {
        boolean isOK = true;

        String campaignName = campaignNameTextField.getText();
        if (campaignName == null || campaignName.length() < 3 || campaignName.length() > 25) {
            emptyField("campaignName");
            isOK = false;
        } else {
            campaignNameError.setText("");
        }

        String campaignType = choseType.getValue();
        if (campaignType == null) {
            emptyField("campaignType");
            isOK = false;
        } else {
            typeError.setText("");
        }

        String location = insertLocation.getText();
        if (location == null || location.length() < 3 || location.length() > 25) {
            emptyField("location");
            isOK = false;
        } else {
            locationError.setText("");
        }

        LocalDate date = startDatePicker.getValue();
        if (date == null) {
            emptyField("date");
            isOK = false;
        } else {
            timeDateError.setText("");
        }

        String freq = choseFreq.getValue();
        if (freq == null) {
            emptyField("freq");
            isOK = false;
        } else {
            freqError.setText("");
        }

        return isOK;
    }

    private logic.beans.BeanCampaign buildCampaignData(int userID) throws InvalidValueException, TextTooLongException {
        logic.beans.BeanCampaign campaignData = new logic.beans.BeanCampaign();

        // Lettura campi
        String campaignName = campaignNameTextField.getText();
        String campaignType = choseType.getValue();
        String location = insertLocation.getText();
        LocalDate date = startDatePicker.getValue();
        String freq = choseFreq.getValue();

        Integer hours = hourSpinner.getValue();
        Integer minutes = minuteSpinner.getValue();
        LocalTime time = LocalTime.of(hours != null ? hours : 0, minutes != null ? minutes : 0);
        LocalDateTime dateTime = LocalDateTime.of(date, time);

        Integer maxPlayers = maxPlayersSpinner.getValue();

        // Popolamento Bean
        campaignData.setCampName(campaignName);
        campaignData.setCampDate(dateTime);
        campaignData.setCampFreq(freq);
        campaignData.setMaxNumberOfPlayers(maxPlayers);
        campaignData.setTimeSession(time);
        campaignData.setDmId(userID);


        switch (campaignType) {
            case TYPE_OFFLINE:
                campaignData.setCampMode(Mode.OFFLINE);
                campaignData.setCampCity(location);
                break;
            case TYPE_ONLINE:
                campaignData.setCampMode(Mode.ONLINE);
                campaignData.setPlatform(location);
                break;
            default:
                logger.log(Level.SEVERE, "Campaign type not found");
        }

        return campaignData;
    }

    private void loadNextScene(logic.beans.BeanCampaign campaignData, ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/invitePlayers.fxml")); //QUI
        Parent root = loader.load();

        GCInvitePlayers nextGraphicController = loader.getController();
        nextGraphicController.receiveCampaign(campaignData);

        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }


    private void emptyField(String field){
        switch (field){
            case "campaignName":
                campaignNameError.setText("Il nome della campagna deve essere lungo tra 3 e 25 caratteri");
                break;
            case "campaignType":
                typeError.setText("Devi scegliere se offline o online");
                break;
            case "location":
                locationError.setText("Se la campagna è offline devi inserire la citta'");
                break;
            case "date":
                timeDateError.setText("Devi inserire una data valida");
                break;
            case "freq":
                freqError.setText("Devi scegliere la frequenza");
                break;
            default:
                break;
        }
    }
}