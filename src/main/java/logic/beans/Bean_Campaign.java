import java.time.LocalTime;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;



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
    private List<PlayerBean> acceptedPlayers;


}

// il seguente metodo aggiorna la lista di players in attesa di approvazione per la partecipazione. Idea:
// prendo la lista esistente, controllo le richieste e l'aggiorno. Come? La lista è aggiornata da "Gestisci richiesta
// player", che interagisce con il database. Teoricamente la bean, tramite la model, interagisce con la DAO per ripescare
// le informazioni


public Bean_Campaign(int maxNumberOfPlayers){
    this.maxNumberOfPlayers = maxNumberOfPlayers;
    this.acceptedPlayers = new ArrayList<>();
}

public int getAcceptedPlayers(){
    return this.getAcceptedPlayers.size();
}

public boolean IsFull(){
    return getAcceptedPlayers() >= this.maxNumberOfPlayers;
} //se è vero, disabilitare bottone per richiedere partecipazione/inviare una notifica

public String getNumberSeats() {
    return getAcceptedPlayers() + "/" + this.maxNumberOfPlayers + " giocatori";
}

// setters
public void setCampId(int campaignId){
    this.campaignId = campaignId;
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

// getters

public int getCampId(){
    return this.campaignID;
}

public String getCampName(){
    return this.campaignName;
}

public String getDM(){
    return this.DM;
}

public String getCampDate(){
    return this.date;
}

public String getCampTimeSession(){
    return this.timeSession;
}


///

public boolean IsModeOnline(String mode){
    if (Objects.equals(mode, "online")){
        return true; // la campagna si svolge online
    }else{
        return false;
    }
}
public void setCampCity(String city, String mode) { /*throws InvalidValueException {
    if(city == null){
        throw new InvalidValueException("Please insert a valid city");
    }*/
    if (IsModeOnline(mode) == true) {
        this.city = city;
    } else {
        this.city = null; //????
    }
}

public String getCampCity(){
    return this.city;
}


