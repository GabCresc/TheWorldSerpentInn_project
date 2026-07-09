package logic.beans;
import java.time.LocalDateTime;
import logic.utils.enums.NotificationTypes;

public class BeanNotificationData {
    private int userID;
    private int campaignID;
    private LocalDateTime date; // per i reminder
    private LocalDateTime startDate;
    private String message;
    private NotificationTypes type;

    public BeanNotificationData(String message, NotificationTypes type, int userID, int campaignID){
        this.message = message;
        this.type = type;
        this.userID = userID;
        this.campaignID = campaignID;
    }
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

    public LocalDateTime getStartDate() {
        return this.startDate;
    }

    public String getMessage(){
        return this.message;
    }

    public NotificationTypes getType(){
        return this.type;
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

    public void setStartDate(LocalDateTime startDate){
        this.startDate = startDate;
    }

    public void setMessage(String message) throws IllegalArgumentException{
        if(message == null){
            throw new IllegalArgumentException("Notification message can't be null"); //da gestire nel NotificationControl
        }
        this.message = message;
    }

    public void setType(NotificationTypes type)throws IllegalArgumentException {
        if (type == null) {
            throw new IllegalArgumentException("Notification Type can't be null"); //da gestire nel NotificationControl
        }
        this.type = type;
    }
}
