package logic.beans;

import logic.utils.Notification_Types;
import logic.utils.User_Types;

public class Bean_Notification {

    private String notified_name;
    private String notifier_name;
    private Integer notification_id;
    private User_Types user_types;
    private Notification_Types notification_types;

    // SETTERS

    public void setNotifier_name(String notifier_name) {
        this.notifier_name = notifier_name;
    }

    public void setNotified_name(String notified_name) {
        this.notified_name = notified_name;
    }

    public void setNotification_ID(Integer notification_ID) {
        this.notification_id = notification_ID;
    }

    public void setNotificationType(Notification_Types type_notif){
            this.notification_types = type_notif;
    }

    public void setUserType(User_Types type_user){
          this.user_types = type_user;
     }

    //GETTERS

    public String getNotifier_name() {
        return this.notifier_name;
    }
    public String getNotified_name() {
        return this.notified_name;
    }

    public int getNotification_ID() {
        return this.notification_id;
    }

    public Notification_Types getNotificationType() {
        return this.notification_types;
    }

    public User_Types getUserType(){
         return this.user_types;
    }
}



