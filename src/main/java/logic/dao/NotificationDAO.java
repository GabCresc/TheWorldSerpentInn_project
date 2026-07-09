package logic.dao;

import logic.model.Notification;

import java.io.Serializable;
import java.util.ArrayList;

public interface NotificationDAO {

    int addNotification(Notification msg);

    ArrayList<Notification> getNotificationsByUserId(int userId);
    boolean deleteNotification(int notificationId);
}