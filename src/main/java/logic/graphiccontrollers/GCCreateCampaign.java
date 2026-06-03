package logic.graphiccontrollers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import logic.view.EssentialGUI;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class GCCreateCampaign extends EssentialGUI {
    @FXML
    private TextField campaignNameTextField;
    @FXML
    private ChoiceBox<String> choseType;
    @FXML
    private TextField insertCity;
    private String[] types={"offline", "online"};
    @FXML
    DatePicker startDatePicker;
    @FXML
    private Spinner<Integer> hourSpinner;
    @FXML
    private Spinner<Integer> minuteSpinner;
    @FXML
    private ChoiceBox<String> choseFreq;
    private String[] freqTypes={"settimanale", "bisettimanale", "mensile"};
    @FXML
    private Spinner<Integer> maxPlayersSpinner;

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


    public void confirmCampaignCreation(){
        String campaignName=campaignNameTextField.getText();
        String campaignType=choseType.getValue();
        String city=insertCity.getText();
        LocalDate date=startDatePicker.getValue();
        Integer hours=hourSpinner.getValue();
        Integer minutes=minuteSpinner.getValue();
        LocalTime time=LocalTime.of(hours, minutes);
        LocalDateTime dateTime=LocalDateTime.of(date,time);
        String freq=choseFreq.getValue();
        Integer maxPlayers=maxPlayersSpinner.getValue();

    }
}
