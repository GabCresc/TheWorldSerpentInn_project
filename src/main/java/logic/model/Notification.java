package logic.model;

import logic.utils.enums.NotificationTypes;
import logic.utils.enums.UserTypes;

public interface Notification {
    Notification_Types getNotificationType();
    String getNotifier_Name();
    String getNotified_Name();
    int getNotification_ID();
    User_Types getUserType();
    int getUser_ID();

    void setNotificationType(Notification_Types notification_type);
    void setNotifier_Name(String notifier_name);
    void setNotified_Name(String notified_name);
    void setNotification_ID(int notification_ID);
    void setUserType(User_Types user_type);
    void setUser_ID(int user_ID)
}
