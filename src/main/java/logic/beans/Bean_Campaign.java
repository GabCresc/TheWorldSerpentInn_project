package logic.beans;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
        private String mode;
        private List<UserBean> waitingPlayers = new ArrayList<>();
        private List<UserBean> acceptedPlayers = new ArrayList<>();


// Il metodo getAcceptedPlayers restituisce la lista dei giocatori già accettati, permettendo di verificare se ci sono posti
    // ancora disponibili. Ciò è utile per verificare se è possibile mandare una richiesta di partecipazione

    public List<UserBean> getAcceptedPlayers() {
        return this.acceptedPlayers;
    }
    public boolean IsFull(){ // questo metodo controlla se la campagna è piena
        return getAcceptedPlayers().size() >= this.maxNumberOfPlayers;
    } //se è vero, disabilitare bottone per richiedere partecipazione/inviare una notifica

    public String getNumberSeats() { //metodo per stampare a schermo il numero di giocatori (necessario?)
        return getAcceptedPlayers() + "/" + this.maxNumberOfPlayers + " giocatori accettati";
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

    public void setCampDate(String date){ /*throws InvalidValueException {
    if(city == null){
        throw new InvalidValueException("Please insert a valid city");
    }*/
        this.date = date;
    }

    public void setTimeSession(String timeSession){
        this.timeSession = timeSession; //da espandere
    }

    public void SetCampMode(String mode){
        this.mode = mode;
    }

    public void SetCampDay(String day){
        this.day = day;
    }

    public void SetCampDMId (int dmID){
        this.dmID = dmID;
    }


// GETTERS

    public int getCampId(){
        return this.campaignID;
    }

    public String getCampName(){
        return this.campaignName;
    }

    public String getCampDM(){
        return this.DM;
    }

    public String getCampDate(){
        return this.date;
    }

    public String getCampTimeSession(){
        return this.timeSession;
    }

    public String getCampDay(){
        return this.day;
    }

    public int getCampDMID(){
        return this.dmID;
    }

    public String getCampMode(){
        return this.mode;
    }

    // per gestire la richiesta di partecipazione, è anche possibile mettere una campagna con 0 posti: in tal modo
    // nel momento in cui il player manda una richiesta, appare un pop up che lo informa che la campagna è piena e
    // viene reinderizzato nella homepage

    ///

    public String getCampCity(){
        return this.city;
    }

    public Integer getMaxNumberOfPlayers(){
        return this.maxNumberOfPlayers;
    }


}
