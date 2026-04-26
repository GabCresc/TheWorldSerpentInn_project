package logic.controllers.factory;

import logic.model.Notification;
import logic.model.Local_Notification;
import logic.model.Server_Notification;
import logic.utils.enums.NotificationTypes;
import logic.utils.enums.Notification_Kind;
import logic.utils.enums.UserTypes;

public class NotificationFactory {
    public Notification CreateLocalNotification(int notification_id, int notifier_id, int notified_id, NotificationTypes type, int campaign_id) {
        return new Local_Notification(notification_id, notifier_id, notified_id, type, campaign_id);
    }

    public Notification CreateServerNotification(int notification_id, int notifier_id, int notified_id, NotificationTypes type, int campaign_id) {
        return new Server_Notification(notification_id, notifier_id, notified_id, type, campaign_id);
    }

    public Notification CreateNotification(int notification_id, int notifier_id, int notified_id, NotificationTypes type, int campaign_id) {
        Notification_Kind kind = type.getKind();
        if (kind == Notification_Kind.LOCAL) {
            return CreateLocalNotification(notification_id, notifier_id, notified_id, type, campaign_id);
        } else {
            return CreateServerNotification(notification_id, notifier_id, notified_id, type, campaign_id);
        }
    }
}
