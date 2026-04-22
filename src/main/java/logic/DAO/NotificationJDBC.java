package logic.DAO;

import logic.model.Notification;
import logic.utils.enums.NotificationTypes;
import logic.utils.SingletonDBSession;
import logic.controllers.factory.NotificationFactory;
import lombok.extern.java.Log;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.sql.*;
import java.util.logging.Level; // suggerito da SonarCloud


@Log
public class NotificationJDBC implements NotificationDAO {
    private NotificationFactory notiFactory;

    public NotificationJDBC(){
        notiFactory = new NotificationFactory(); // istanziamo l'istanza concreta di factory
    }
    @Override

    public void addNotification(NotificationTypes type, String notifier_name, String notified_name,
                                int notification_id, int user_id, int campaign_id) {
        try(Connection conn = SingletonDBSession.getInstance().startConnection()){
            PreparedStatement pstat = conn.prepareStatement("INSERT INTO notification (notificationID, notified_name, notifier_name, type, userID, campaignID)");
            pstat.setInt(1, notification_id);
            pstat.setString(2, notified_name);
            pstat.setString(3, notifier_name);
            pstat.setString(4, String.valueOf(type));
            pstat.setInt(5, user_id);
            pstat.setInt(6, campaign_id);

            pstat.executeUpdate();

        } catch (SQLException e) {
            log.log(Level.SEVERE, "SQL exception: problem in adding notification");

        } finally {
             SingletonDBSession.getInstance().closeConnection();
        }
        }

    @Override
    public ArrayList<Notification> getNotificationsByUserId(int user_id){
        ArrayList<Notification> notif_list = new ArrayList<>();
         try(Connection conn = SingletonDBSession.getInstance().startConnection()){
            PreparedStatement ps = conn.prepareStatement("SELECT notificationID, notified_name, notifier_name, type, campaignID FROM notification WHERE userID = ?");
            ps.setInt(1, user_id);
            try(ResultSet rs = ps.executeQuery()){
                while(rs.next()){
                int notifID = rs.getInt("notificationID");
                String notified = rs.getString("notified_name"); //da cambiare se vogliamo usare ID
                String notifier = rs.getString("notifier_name");
                String notiftype = rs.getString("type");
                int notifcampaignID = rs.getInt("campaignID");

                NotificationTypes typ = NotificationTypes.valueOf(notiftype);

                Notification msg = notiFactory.CreateNotification(notifID, notified, notifier, typ, user_id, notifcampaignID);

                /*if(kind == Notification_Kind.LOCAL){
                    msg = notiFactory.CreateLocalNotification(); // però dovrebbe prendere come param kind o type per sapere che il type
                }else{
                    // notifica di tipo server
                    msg = notiFactory.CreateServerNotification();
                }*/

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



