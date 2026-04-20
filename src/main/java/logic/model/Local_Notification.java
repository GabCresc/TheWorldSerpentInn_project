package com.worldserpentinn.model;

public class Local_Notification implements Notification{
    private User_Types user_type;
    private Notification_Types notification_type;
    private String notified_name;
    private String notifier_name;
    private int notification_ID;
    private int user_ID;

    @Override
    public Notification_Types getNotificationType(){
        return notification_type;
    }

    @Override
    public String getNotifier_Name(){
        return notifier_name;
    }

    @Override
    public String getNotified_Name(){
        return notified_name;
    }

    @Override
    public int getNotification_ID(){
        return notification_ID;
    }

    @Override
    public User_Types getUserType(){
        return user_type;
    }

    @Override
    public int getUser_ID(){
        return user_ID;
    }


    @Override
    public void setNotificationType(Notification_Types notification_type){
        this.notification_type = notification_type;
    }

    @Override
    public void setNotifier_Name(String notifier_name){
        this.notifier_name = notifier_name;
    }

    @Override
    public void setNotified_Name(String notified_name){
        this.notified_name = notified_name;
    }

    @Override
    public void setNotification_ID(int notification_ID){
        this.notification_ID = notification_ID;
    }

    @Override
    public void setUserType(User_Types user_type){
        this.user_type = user_type;
    }

    @Override
    public void setUser_ID(int user_ID){
        this.user_ID=user_ID;
    }
}
