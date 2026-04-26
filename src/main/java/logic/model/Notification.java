package logic.model;

import logic.utils.enums.NotificationTypes;
import logic.utils.enums.UserTypes;

public interface Notification {
    NotificationTypes getNotificationType();
    int getNotifier_id();
    int getNotified_id();
    int getNotification_ID();
    UserTypes getNotifierType();
    UserTypes getNotifiedType();
    int getCampaign_id();

    void setNotificationType(NotificationTypes notification_type);
    void setNotifier_id(int notifier_id);
    void setNotified_id(int notified_id);
    void setNotification_ID(int notification_ID);
    void setNotifierType(UserTypes notifier_type);
    void setNotifiedType(UserTypes notified_type);
    void setCampaign_id(int campaign_id);
}
