package com.worldserpentinn.model;

public interface Notification {
    String getNotificationType();
    String getNotifier_Name();
    String getNotified_Name();
    int getNotification_ID();
    User_Types getUserType();

    void setNotificationType(Notification_Types notification_type);
    void setNotifier_Name(String notifier_name);
    void setNotified_Name(String notified_name);
    void setNotification_ID(int notification_ID);
    void setUserType(User_Types user_type);
}