package logic.controllers.factory;

import logic.model.Notification;
import logic.model.LocalNotification;
import logic.model.ServerNotification;
import logic.utils.enums.NotificationTypes;
import logic.utils.enums.NotificationKind;

public class NotificationFactory {

    public Notification createLocalNotification(int notificationID, int notifierID, int notifiedID, NotificationTypes type, int campaignID) {
        return new LocalNotification(notificationID, notifierID, notifiedID, type, campaignID);
    }

    public Notification createServerNotification(int notificationID, int notifierID, int notifiedID, NotificationTypes type, int campaignID) {
        return new ServerNotification(notificationID, notifierID, notifiedID, type, campaignID);
    }

    public Notification createNotification(int notificationID, int notifierID, int notifiedID, NotificationTypes type, int campaignID) {
        NotificationKind kind = type.getKind();
        if (kind == NotificationKind.LOCAL) {
            return createLocalNotification(notificationID, notifierID, notifiedID, type, campaignID);
        } else {
            return createServerNotification(notificationID, notifierID, notifiedID, type, campaignID);
        }
    }
}