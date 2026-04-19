package com.worldserpentinn.model;

public class Server_Notification {
    private User_Types user_type;
    private Notification_Types notification_type;
    private String notified_name;
    private String notifier_name;
    private int notification_ID;
    private int user_ID;


    public Notification_Types getNotificationType(){
        return notification_type;
    }

    public String getNotifier_Name(){
        return notifier_name;
    }

    public String getNotified_Name(){
        return notified_name;
    }

    public int getNotification_ID(){
        return notification_ID;
    }

    public User_Types getUserType(){
        return user_type;
    }

    public int getUser_ID(){
        return user_ID;
    }


    public void setNotificationType(Notification_Types notification_type){
        this.notification_type = notification_type;
    }

    public void setNotifier_Name(String notifier_name){
        this.notifier_name = notifier_name;
    }

    public void setNotified_Name(String notified_name){
        this.notified_name = notified_name;
    }

    public void setNotification_ID(int notification_ID){
        this.notification_ID = notification_ID;
    }

    public void setUserType(User_Types user_type){
        this.user_type = user_type;
    }

    public void setUser_ID(int user_ID){
        this.user_ID=user_ID;
    }
}
