package logic.model;

import logic.utils.enums.NotificationTypes;
import logic.utils.enums.UserTypes;

public class Local_Notification implements Notification{
    private UserTypes notifier_type;
    private UserTypes notified_type;
    private NotificationTypes notification_type;
    private int notified_id;
    private int notifier_id;
    private int notification_ID;
    private int campaign_id;


    public Local_Notification(int notification_id, int notifier_id, int notified_id, NotificationTypes type, int campaign_id){
        this.notifier_type=notifier_type;
        this.notified_type=notified_type;
        this.notification_ID=notification_id;
        this.notifier_id = notifier_id;
        this.notified_id=notified_id;
        this.notification_type=type;
        this.campaign_id=campaign_id;
    }

    //getter
    @Override
    public NotificationTypes getNotificationType(){
        return notification_type;
    }

    @Override
    public int getNotifier_id(){
        return notifier_id;
    }

    @Override
    public int getNotified_id(){
        return notified_id;
    }

    @Override
    public int getNotification_ID(){
        return notification_ID;
    }

    @Override
    public UserTypes getNotifierType(){
        return  notifier_type;
    }
    @Override
    public UserTypes getNotifiedType(){
        return notified_type;
    }

    @Override
    public int getCampaign_id(){
        return campaign_id;
    }

    //setter
    @Override
    public void setNotificationType(NotificationTypes notification_type){
        this.notification_type=notification_type;
    }

    @Override
    public void setNotifier_id(int notifier_id){
        this.notifier_id = notifier_id;
    }

    @Override
    public void setNotified_id(int notified_id){
        this.notified_id = notified_id;
    }

    @Override
    public void setNotification_ID(int notification_ID){
        this.notification_ID = notification_ID;
    }

    @Override
    public void setNotifierType(UserTypes notifier_type){
        this.notifier_type=notifier_type;
    }

    @Override
    public void setNotifiedType(UserTypes notified_type){
        this.notified_type=notified_type;
    }

    @Override
    public void setCampaign_id(int campaign_id){
        this.campaign_id=campaign_id;
    }
}
