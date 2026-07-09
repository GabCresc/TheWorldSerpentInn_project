package logic.beans;

import java.util.ArrayList;
import java.util.List;

import logic.model.ModelCampaign;
import logic.utils.enums.Mode;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class BeanCampaign {

    private String campaignName;
    private LocalTime timeSession;
    private LocalDateTime startDate;
    private String city;
    private String freq;
    private String dm;
    private Integer maxNumberOfPlayers;
    private Integer campaignID;
    private Integer dmID;
    private Mode mode;
    private List<BeanUser> waitingPlayers = new ArrayList<>();
    private List<BeanUser> acceptedPlayers = new ArrayList<>();
    private String platform;

    public BeanCampaign(){
        // empty
    }

    public BeanCampaign(ModelCampaign model){
        this.campaignID = model.getCampId();
        this.campaignName = model.getCampName();
        this.timeSession = model.getCampTimeSession();
        this.startDate = model.getCampStartDate();
        this.city = model.getCampCity();
        this.dm = model.getCampDm();
        this.maxNumberOfPlayers = model.getMaxPlayers();
        this.dmID = model.getCampDmId();
        this.mode = model.getCampMode();
        this.platform = model.getPlatform();
        this.freq = model.getCampFreq();
    }

    // SETTERS
    public void setCampId(int campaignId){
        this.campaignID = campaignId;
    }

    public void setCampName(String campaignName){
        this.campaignName = campaignName;
    }

    public void setDmId(int dmID){
        this.dmID = dmID;
    }

    public void setMaxNumberOfPlayers(int maxNumberOfPlayers){
        this.maxNumberOfPlayers = maxNumberOfPlayers;
    }
    public void setDM(String dm){
        this.dm = dm;
    }
    public void setCampDate(LocalDateTime date){this.startDate = date;}

    public void setTimeSession(LocalTime timeSession){
        this.timeSession = timeSession;
    }
    public void setCampMode(Mode mode){this.mode = mode;}
    public void setCampFreq(String freq){this.freq = freq;}
    public void setCampCity(String city){this.city = city;}
    public void setWaitingPlayers(List<BeanUser> waitingPlayers){this.waitingPlayers = waitingPlayers;}
    public void setAcceptedPlayers(List<BeanUser> acceptedPlayers){this.acceptedPlayers = acceptedPlayers;}
    public void setPlatform(String platform){this.platform = platform;}

    // GETTERS

    // Il metodo getAcceptedPlayers restituisce la lista dei giocatori già accettati, permettendo di verificare se ci sono posti
    // ancora disponibili. Ciò è utile per verificare se è possibile mandare una richiesta di partecipazione

    public List<BeanUser> getAcceptedPlayers() {return this.acceptedPlayers;}

    public List<BeanUser> getWaitingPlayers() {return this.waitingPlayers;}

    public boolean isFull(){return getAcceptedPlayers().size() >= this.maxNumberOfPlayers;}


    public int getCampId(){return (this.campaignID != null) ? this.campaignID : -1;}

    public String getCampName(){return this.campaignName;}

    public String getCampDM(){return this.dm;}

    public LocalDateTime getCampDate(){return this.startDate;}

    public LocalTime getCampTimeSession(){return this.timeSession;}

    public String getCampFreq(){return this.freq;}

    public int getCampDMID(){return (this.dmID != null) ? this.dmID : 0;}

    public Mode getCampMode(){return this.mode;}

    public String getCampCity(){return this.city;}

    public Integer getMaxNumberOfPlayers(){return this.maxNumberOfPlayers;}

    public String getPlatform() {return this.platform;}


}