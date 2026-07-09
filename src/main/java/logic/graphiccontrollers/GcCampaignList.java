package logic.graphiccontrollers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import logic.beans.BeanCampaign;
import logic.beans.BeanFilter;
import logic.controllers.CampaignParticipationControl;
import logic.utils.enums.Mode;
import javafx.geometry.Pos;

import javafx.event.ActionEvent;
import logic.view.EssentialGUI;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GcCampaignList extends EssentialGUI {

    @FXML
    private MenuItem logoutItem;

    @FXML
    private TextField filterField;

    @FXML
    private ComboBox<String> filterMenu;

    @FXML
    private Button searchButton;

    @FXML
    private MenuButton menu;

    @FXML
    private Pagination pagination;

    @FXML
    private Pane scalesBackground;

    @FXML
    private Hyperlink showCampaigns;

    @FXML
    private Hyperlink showCharacters;

    @FXML
    private Hyperlink showCreateCampaigns;

    @FXML
    private Hyperlink showCreateCharacter;

    @FXML
    private Label errorLabel;

    private static final Logger logger = Logger.getLogger(GcCampaignList.class.getName());
    private static final int ITEMS = 6; //massimo sei campagne a pagina
    private List<BeanCampaign> campaigns = new ArrayList<>(); //lista con tutte le campagne
    private CampaignParticipationControl campaignControl = new CampaignParticipationControl();

    private static final String TUTTE = "TUTTE";

    @FXML
    public void initialize(){ //inizializiamo il menu di scelta
        filterMenu.getItems().addAll("ONLINE", "OFFLINE", TUTTE);
        filterMenu.setValue(TUTTE);
        filterField.setPromptText("Cerca per nome: ");
        BeanFilter beanFilter = new BeanFilter();
        beanFilter.setMode(null);
        refresh(beanFilter);
    }

    private void refresh(BeanFilter bean){

        if((bean.getNameCampaign() == null || bean.getNameCampaign().isEmpty()) && bean.getMode() == null){
            this.campaigns = campaignControl.getAvailableCampaigns();
        }else{
            this.campaigns = campaignControl.getFilteredCampaigns(bean);
        }

        //logica per aggiornare pagination
        int count = (campaigns.size() + ITEMS -1)/ITEMS;
        if(count == 0){
            count = 1;
        }

        pagination.setPageCount(count);

        pagination.setPageFactory(this::createPage); //cosa fare quando l'utente cambia pagina
    }

    public void searchAction(ActionEvent event) {
        String searchtext = filterField.getText();
        String mode = filterMenu.getValue();

        BeanFilter newFilter = new BeanFilter();
        newFilter.setNameCampaign(searchtext);

        if (TUTTE.equals(mode)) {
            newFilter.setMode(null);
        } else {
            newFilter.setMode(Mode.valueOf(mode));
        }
        refresh(newFilter);
    }


    private Node createPage(int index){ //index è l'indice della pagina per cui setPageFactory dovrebbe creare il nodo, che mostra il contenuto della pagina
        GridPane grid = new GridPane();
        grid.setHgap(20);
        grid.setVgap(20);

        grid.setAlignment(Pos.CENTER);

        int start = index*ITEMS;
        int end = Math.min(start + ITEMS, campaigns.size());
        int counter = 0;

        for(int i = start; i < end; i++){
            BeanCampaign bean = campaigns.get(i);

            Node campaignCard = loadCampaignCard(bean);
            campaignCard.setStyle("-fx-background-color: white; -fx-border-color: green; -fx-border-width: 4");

            //per disporre le card in una griglia 2x3
            int column = counter%3;
            int row =  counter/3;

            if(campaignCard != null) {
                grid.add(campaignCard, column, row);
            }
            counter++;
        }

        return grid;
    }

    private Node loadCampaignCard(BeanCampaign bean){
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/campaignCard.fxml"));
            Node card = loader.load();

            GcCampaignCard cardController = loader.getController();
            cardController.setData(bean); //passiamo i dati alla campaignCard

            return card;
        }catch(Exception e){
            logger.log(Level.WARNING, "Something went wrong while loading a campaign card", e);
            errorLabel.setText("Qualcosa è andato storto durante il caricamento della campagna");
            errorLabel.setStyle("-fx-text-fill: #ff0000");
            return null;
        }
    }

}