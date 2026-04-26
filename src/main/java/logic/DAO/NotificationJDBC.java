package logic.DAO;

import logic.model.Notification;
import logic.utils.enums.NotificationTypes;
import logic.utils.SingletonDBSession;
import lombok.extern.java.Log;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.sql.*;
import java.util.logging.Level; // suggerito da SonarCloud
import logic.controllers.factory.NotificationFactory;


@Log
public class NotificationJDBC implements NotificationDAO {
    private NotificationFactory notiFactory;

    public NotificationJDBC(){
        notiFactory = new NotificationFactory(); // istanziamo l'istanza concreta di factory
    }
    @Override

    public void addNotification(NotificationTypes type, int notifierID, int notifiedID,
                                int notification_id, int campaign_id) {
        try(Connection conn = SingletonDBSession.getInstance().startConnection()){
            PreparedStatement pstat = conn.prepareStatement("INSERT INTO notification (notificationID, notifiedID, notifierID, type, campaignID)");
            pstat.setInt(1, notification_id);
            pstat.setInt(2, notifiedID);
            pstat.setInt(3, notifierID);
            pstat.setString(4, String.valueOf(type));
            pstat.setInt(6, campaign_id);

            pstat.executeUpdate();

        } catch (SQLException e) {
            log.log(Level.SEVERE, "SQL exception: problem in adding notification");

        } finally {
             SingletonDBSession.getInstance().closeConnection();
        }
        }

    @Override
    public ArrayList<Notification> getNotificationsByUserId(int notified_ID){ //colui che viene notificato è l'utente che ci interessa per recuperare le notifiche
        ArrayList<Notification> notif_list = new ArrayList<>();
         try(Connection conn = SingletonDBSession.getInstance().startConnection()){
            PreparedStatement ps = conn.prepareStatement("SELECT notificationID notifierID, type, campaignID FROM notification WHERE notifiedID = ?");
            ps.setInt(1, notified_ID);
            try(ResultSet rs = ps.executeQuery()){
                while(rs.next()){
                int notifID = rs.getInt("notificationID");
                int notifier = rs.getInt("notifierID");
                String notiftype = rs.getString("type");
                int notifcampaignID = rs.getInt("campaignID");

                NotificationTypes typ = NotificationTypes.valueOf(notiftype);

                Notification msg = notiFactory.CreateNotification(notifID, notified_ID, notifier, typ, notifcampaignID);

                notif_list.add(msg);

                }
         }
    }catch(SQLException e){
           log.log(Level.SEVERE, "SQL Exception occured while getting notifications by UserID");
        }
        return notif_list;
    }

    @Override
    public boolean deleteNotification(int notification_id) {
        try(Connection conn = SingletonDBSession.getInstance().startConnection()) {
            PreparedStatement ps = conn.prepareStatement("DELETE FROM notification WHERE = notification_id ?");
            ps.setInt(1, notification_id);
            ps.execute();
            return true;
        } catch(SQLException e) {
            log.log(Level.SEVERE, "SQL Exception occured while deleting notification");
            return false;
        } finally {

            SingletonDBSession.getInstance().closeConnection();
        }
    }

}



