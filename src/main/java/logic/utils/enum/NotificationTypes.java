package logic.utils.enums;

import lombok.extern.java.Log;
import java.util.logging.Level;

@Log
public enum NotificationTypes {
    CAMPAIGN_ADDED, //cioè sei stato aggiunto alla campagna(?)
    REQUEST_PARTECIPATION,
    ACCEPT_PARTECIPATION,
    REMINDER,
    LOGGED_IN,
    LOGGED_OUT;


    public Notification_Kind getKind(){

        switch(this){
            case REMINDER, LOGGED_IN, LOGGED_OUT:
                return Notification_Kind.LOCAL;

            case CAMPAIGN_ADDED, REQUEST_PARTECIPATION, ACCEPT_PARTECIPATION:
                return Notification_Kind.SERVER;

            default:
                log.log(Level.WARNING, "Not a registered type");
                return null;
        }

    }
}
