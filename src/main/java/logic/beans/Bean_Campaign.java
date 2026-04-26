package logic.beans;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import logic.model.Model_Campaign;
import logic.utils.enums.Mode;
import logic.beans.Bean_User;

public class Bean_Campaign {

        private String campaignName;
        private String timeSession;
        private String date;
        private String city;
        private String day;
        private String DM;
        private Integer maxNumberOfPlayers;
        private Integer campaignID;
        private Integer dmID;
        private Mode mode;
        private List<Bean_User> waitingPlayers = new ArrayList<>();
        private List<Bean_User> acceptedPlayers = new ArrayList<>();

    public Bean_Campaign(){
        // empty
    }

    public Bean_Campaign(Model_Campaign model){
        this.campaignID = model.getCampId();
        this.campaignName = model.getCampName();
        this.timeSession = model.getCampTimeSession();
        this.date = model.getCampDate();
        this.city = model.getCampCity();
        this.DM = model.getCampDM();
        this.maxNumberOfPlayers = model.getMaxPlayers();
        this.dmID = model.getCampDMID();
        this.mode = model.getCampMode();
    }

    // SETTERS
    public void setCampId(int campaignId){
        this.campaignID = campaignId;
    }
    public void setCampName(String campaignName){ /*throws InvalidValueException, TextTooLongException {
    if(eventName == null || eventName.equalsIgnoreCase("")) {
        throw new InvalidValueException("Please insert a valid event name");
    }
    else if(eventName.length() > 20) {
        throw new TextTooLongException("Too many characters for event name field");
    }*/
        this.campaignName = campaignName;
    }
    public void setDmId(int dmID){
        this.dmID = dmID;
    }
    public void setDM(String DM){
        this.DM = DM;
    }
    public void setCampDate(String date){this.date = date;}
    public void setTimeSession(String timeSession){
        this.timeSession = timeSession; //da espandere
    }
    public void SetCampMode(Mode mode){this.mode = mode;}
    public void SetCampDay(String day){this.day = day;}
    public void SetCampDMId (int dmID){this.dmID = dmID;}

// GETTERS

    // Il metodo getAcceptedPlayers restituisce la lista dei giocatori già accettati, permettendo di verificare se ci sono posti
    // ancora disponibili. Ciò è utile per verificare se è possibile mandare una richiesta di partecipazione

    public List<Bean_User> getAcceptedPlayers() {return this.acceptedPlayers;}

    public boolean IsFull(){return getAcceptedPlayers().size() >= this.maxNumberOfPlayers;}
    //se è vero, disabilitare bottone per richiedere partecipazione/inviare una notifica

    public String getNumberSeats() {return getAcceptedPlayers() + "/" + this.maxNumberOfPlayers + " giocatori accettati";}

    public int getCampId(){return this.campaignID;}

    public String getCampName(){return this.campaignName;}

    public String getCampDM(){return this.DM;}

    public String getCampDate(){return this.date;}

    public String getCampTimeSession(){return this.timeSession;}

    public String getCampDay(){return this.day;}

    public int getCampDMID(){return this.dmID;}

    public Mode getCampMode(){return this.mode;}

    // per gestire la richiesta di partecipazione, è anche possibile mettere una campagna con 0 posti: in tal modo
    // nel momento in cui il player manda una richiesta, appare un pop up che lo informa che la campagna è piena e
    // viene reinderizzato nella homepage

    ///

    public String getCampCity(){return this.city;}

    public Integer getMaxNumberOfPlayers(){return this.maxNumberOfPlayers;}


}
