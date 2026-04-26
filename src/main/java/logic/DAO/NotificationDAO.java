package logic.DAO;

import logic.model.Notification;
import logic.utils.enums.NotificationTypes;

import java.sql.SQLException;
import java.util.ArrayList;

public interface NotificationDAO {

    void addNotification(NotificationTypes type, int notifier_id, int notified_id,
                         int notification_id, int campaign_id);

    ArrayList<Notification> getNotificationsByUserId(int user_id);
    boolean deleteNotification(int notification_id);
}
