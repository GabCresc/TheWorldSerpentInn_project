package logic.model;

import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;
import java.time.LocalTime;

import logic.beans.BeanCampaign;
import logic.utils.enums.Mode;


public class ModelCampaign {
    private String campaignName;
    private LocalTime timeSession;
    private LocalDateTime startDate;
    private String city;
    private String freq; // compreso anno, mese ecc
    private String dm;
    private Integer maxNumberOfPlayers;
    private Integer campaignID;
    private Integer dmId;
    private Mode mode;
    private List<User> waitingPlayers = new ArrayList<>();
    private List<User> acceptedPlayers = new ArrayList<>();
    private String platform;

    public ModelCampaign(){
        //empty
    }

    public ModelCampaign(BeanCampaign beanCampaign) {
        this.campaignName=beanCampaign.getCampName();
        this.timeSession=beanCampaign.getCampTimeSession();
        this.startDate=beanCampaign.getCampDate();
        this.city=beanCampaign.getCampCity();
        this.freq=beanCampaign.getCampFreq();
        this.dm=beanCampaign.getCampDM();
        this.maxNumberOfPlayers=beanCampaign.getMaxNumberOfPlayers();
        this.campaignID=beanCampaign.getCampId();
        this.dmId=beanCampaign.getCampDMID();
        this.mode=beanCampaign.getCampMode();
        this.platform = beanCampaign.getPlatform();
    }

    // questo metodo aggiunge il player alla lista di quelli che aspettano
    public void addToWaitingPlayers(User player) {
        if (!isFull() && !isAlreadyInCampaign(player)) {
            this.waitingPlayers.add(player);
        }
    }

    // si controlla se la lista di player in campagna è piena
    public boolean isFull() {
        return acceptedPlayers.size() >= maxNumberOfPlayers;
    }

    // si controlla se il player è già in una delle due liste
    public boolean isAlreadyInCampaign(User player) {
        return acceptedPlayers.contains(player) || waitingPlayers.contains(player);
    }
    public void setCampCity(String city) {
        this.city = city;
    }

    // GETTERS

    public int getCampId() {
        return this.campaignID;
    }

    public String getCampName() {
        return this.campaignName;
    }

    public String getCampDm() {
        return this.dm;
    }

    public LocalDateTime getCampStartDate() {
        return this.startDate;
    }

    public LocalTime getCampTimeSession() {
        return this.timeSession;
    }

    public String getCampFreq() { return this.freq;}

    public int getCampDmId() {
        return (this.dmId != null) ? this.dmId : 0;
    }

    public Mode getCampMode() {
        return this.mode;
    }

    public int getMaxPlayers(){return this.maxNumberOfPlayers;}

    public String getCampCity() { return this.city;}

    public String getPlatform() {return this.platform;}



    // SETTERS

    public void setCampId(int campaignId) {
        this.campaignID = campaignId;
    }

    public void setCampName(String campaignName)  {

        this.campaignName = campaignName;
    }

    public void setCampDmName(String dm) {

        this.dm = dm;
    }

    public void setCampStartDate(LocalDateTime date){
        this.startDate = date;
    }

    public void setTimeSession(LocalTime timeSession) {
        this.timeSession = timeSession;
    }

    public void setCampMode(Mode mode) {
        this.mode = mode;
    }

    public void setCampFreq(String freq) {
        this.freq = freq;
    }

    public void setCampDmId(int dmId) {
        this.dmId = dmId;
    }

    public void setMaxNumberOfPlayers(int maxNumberOfPlayers){
        this.maxNumberOfPlayers = maxNumberOfPlayers;
    }

    public void setPlatform(String platform) {this.platform = platform;}

}