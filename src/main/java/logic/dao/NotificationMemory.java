package logic.dao;

import logic.model.Notification;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NotificationMemory implements NotificationDAO {

    private static final Logger logger = Logger.getLogger(NotificationMemory.class.getName());
    private static final int idCount = 0;
    private static final Map<Integer, Notification> hashNotif = new HashMap<>();

    @Override
    public int addNotification(Notification msg){ //ok
        int newId= idCount+1;
        msg.setNotificationID(newId);
        try {
            hashNotif.put(newId, msg);
        }catch(NullPointerException | IllegalArgumentException e){
            logger.log(Level.SEVERE, "NullPointerException or IllegalArgumentException occurred while adding new notification", e);
        }
        return newId;
    }

    @Override
    public ArrayList<Notification> getNotificationsByUserId(int userID){ //ok
        ArrayList<Notification> listOfNotif = new ArrayList<>();
        for(Notification n : hashNotif.values()){
            if(n.getNotifiedID() == userID){
                listOfNotif.add(n);
            }else{
                logger.log(Level.INFO, "Notifications for user: {0} not found", userID);
            }
        }
        return listOfNotif;
    }

    @Override //ok
    public boolean deleteNotification(int notificationId){
        return hashNotif.remove(notificationId) != null;
    }
}
