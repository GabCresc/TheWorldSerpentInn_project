package logic.model;

import javafx.event.ActionEvent;
import logic.utils.enums.NotificationTypes;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.io.Serializable;

public interface Notification extends Serializable{
    NotificationTypes getNotificationType();
    int getNotifierID();
    int getNotifiedID();
    int getNotificationID();
    int getCampaignID();
    LocalDateTime getStartDate();
    LocalTime getTimeSession();
    String getCity();
    String getUserEmail();
    String getFrequency();

    void setNotificationType(NotificationTypes notificationType);
    void setNotifierID(int notifierID);
    void setNotifiedID(int notifiedID);
    void setNotificationID(int notificationID);
    void setCampaignID(int campaignID);
    void setStartDate(LocalDateTime endDate);
    void setTimeSession (LocalTime timeSession);
    void setCity(String city);
    void setUserEmail(String email);
    void setFrequency(String frequency);
}