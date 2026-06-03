package logic.beans;
import java.time.LocalDateTime;


public class BeanPartecipation {
    private int userID;
    private int campaignID;
    private LocalDateTime date; // per i reminderr
    private LocalDateTime campaignEndDate;

    //GETTER
    public int getUserID() {
        return this.userID;
    }

    public int getCampaignID() {
        return this.campaignID;
    }

    public LocalDateTime getDate() {
        return this.date;
    }

    public LocalDateTime getEndDate() {
        return this.campaignEndDate;
    }

    //SETTER
    public void setUserID(int userID) {
        this.userID = userID;
    }

    public void setCampaignID(int campaignID) {
        this.campaignID = campaignID;
    }

    public void setDate(LocalDateTime date){
        this.date = date;
    }

    public void setCampaignEndDate(LocalDateTime campaignEndDate){
        this.campaignEndDate = campaignEndDate;
    }

}


