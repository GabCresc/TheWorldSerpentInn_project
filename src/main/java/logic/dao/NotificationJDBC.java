package logic.dao;

import logic.model.Notification;
import logic.utils.enums.NotificationTypes;
import logic.utils.SingletonDBSession;
import logic.controllers.factory.NotificationFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.sql.*;
import java.util.logging.Level; // suggerito da SonarCloud
import java.util.logging.Logger;

public class NotificationJDBC implements NotificationDAO {

    private static final Logger logger = Logger.getLogger(NotificationJDBC.class.getName());
    private NotificationFactory notiFactory;

    public NotificationJDBC(){
        notiFactory = new NotificationFactory(); // istanziamo l'istanza concreta di factory
    }
    @Override

    public int addNotification(Notification msg) { //ok
        try(Connection conn = SingletonDBSession.getInstance().startConnection();
                PreparedStatement pstat = conn.prepareStatement("INSERT INTO notification " +
                "(notifiedID, notifierID, notification_type, campaignID) VALUES (?,?,?,?) ", Statement.RETURN_GENERATED_KEYS);){

            pstat.setInt(1, msg.getNotifiedID());
            pstat.setInt(2, msg.getNotifierID());
            pstat.setString(3, String.valueOf(msg.getNotificationType()));
            pstat.setInt(4, msg.getCampaignID());

            int affectedRows = pstat.executeUpdate();
            if (affectedRows > 0) {
                // Recuperiamo la chiave generata
                try (ResultSet generatedKeys = pstat.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int generatedId = generatedKeys.getInt(1);
                        // Aggiorniamo l'oggetto msg così ha l'ID corretto
                        msg.setNotificationID(generatedId);
                        return generatedId;
                    }
                }
            }

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "SQL exception: problem in adding notification", e);
        }
        return -1;
    }

    //ok
    @Override
    public ArrayList<Notification> getNotificationsByUserId(int notifiedID){ //colui che viene notificato è l'utente che ci interessa per recuperare le notifiche
        ArrayList<Notification> notifList = new ArrayList<>();
        try(Connection conn = SingletonDBSession.getInstance().startConnection()){
            PreparedStatement ps = conn.prepareStatement("SELECT notif.*, camp.start_date, camp.frequency, camp.time_session FROM notification notif " +
                    "JOIN campaign camp ON notif.campaignID = camp.campaignID WHERE notif.notifiedID = ?");
            ps.setInt(1, notifiedID);
            try(ResultSet rs = ps.executeQuery()){
                while(rs.next()){
                    int notifID = rs.getInt("notificationID");
                    int notifier = rs.getInt("notifierID");
                    String notiftype = rs.getString("notification_type");
                    int notifcampaignID = rs.getInt("campaignID");

                    NotificationTypes typ = NotificationTypes.valueOf(notiftype);

                    Notification msg = notiFactory.createNotification(notifID, notifiedID, notifier, typ, notifcampaignID);

                    // queste variabili servono per i reminder,  ma non vengono utilizzate per istanziare l'oggetto notifica. Lasciarle?
                    Timestamp ts = rs.getTimestamp("start_date"); // TimeStamp coincide con il formato di DATETIME() nel database
                   // if(ts != null){msg.setStartDate(ts.toLocalDateTime());}

                    String frequency = rs.getString("frequency");

                    Time timeSession = rs.getTime("time_session");
                    if(timeSession != null){msg.setTimeSession(timeSession.toLocalTime());}

                    notifList.add(msg);

                }
            }
        }catch(SQLException e){
            logger.log(Level.SEVERE, "SQL Exception occurred while getting notifications by UserID", e);
        }
        return notifList;
    }

    @Override
    public boolean deleteNotification(int notificationID) {
        try (Connection conn = SingletonDBSession.getInstance().startConnection()) {
            PreparedStatement ps = conn.prepareStatement("DELETE FROM notification WHERE notificationID = ?");
            ps.setInt(1, notificationID);
            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "SQL Exception occurred while deleting notification", e);
            return false;
        }
    }
}


