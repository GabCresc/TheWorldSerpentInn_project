package logic.utils.enums;

import java.util.logging.Logger;
import java.util.logging.Level;


public enum NotificationTypes {
    CAMPAIGN_ADDED,
    REQUEST_PARTICIPATION,
    ACCEPT_PARTICIPATION,
    REMINDER,
    LOGGED_IN,
    LOGGED_OUT;

    private static final Logger logger = Logger.getLogger(NotificationTypes.class.getName());

    public NotificationKind getKind(){

        switch(this){
            case REMINDER, LOGGED_IN, LOGGED_OUT:
                return NotificationKind.LOCAL;

            case CAMPAIGN_ADDED, REQUEST_PARTICIPATION, ACCEPT_PARTICIPATION:
                return NotificationKind.SERVER;

            default:
                logger.log(Level.WARNING, "Not a registered type");
                return null;
        }

    }
}