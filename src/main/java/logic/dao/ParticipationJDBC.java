package logic.dao;

import logic.utils.SingletonDBSession;
import logic.utils.enums.UserTypes;
import logic.model.User;
import logic.utils.enums.Status;
import logic.exceptions.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.sql.*;
import java.util.List;
import java.util.logging.Level; // suggerito da SonarCloud
import java.util.logging.Logger;

public class PartecipationJDBC {

    private static final Logger logger = Logger.getLogger(PartecipationJDBC.class.getName());

    // questo metodo aggiunge un player nella lista dei waiting players
    public void addWaitingPlayer(int userID, int campaignID) {
        if(isRequestAlreadyPresent(userID, campaignID)){
            throw new RequestAlreadySent("Request already sent to this campaign!");
        } // da ampliare eventualmente
        try (Connection conn = SingletonDBSession.getInstance().startConnection()) {
            PreparedStatement ps = conn.prepareStatement("INSERT INTO campaign_request (campaignID, playerID, status) VALUES (?,?,?)");
            ps.setInt(1, campaignID);
            ps.setInt(2, userID);
            ps.setString(3, Status.WAITING.name());

            ps.executeUpdate();
        } catch (SQLException _) {
            logger.log(Level.SEVERE, "SQLException occurred while adding player to the waiting list");
        }
    }

    public List<Integer> getPlayerIDByStatus(int campaignID, String status) {
        List<Integer> listPlayerID = new ArrayList<>();
        try (Connection conn = SingletonDBSession.getInstance().startConnection()) {
            PreparedStatement ps = conn.prepareStatement("SELECT playerID FROM campaign_request WHERE campaignID = ? and status = ?");
            ps.setInt(1, campaignID);
            ps.setString(2, status);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    listPlayerID.add(rs.getInt("playerID"));
                }
            }

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "SQLException while getting list of players by status", e);
        }
        return listPlayerID;
    }

    public boolean isRequestAlreadyPresent(int userID, int campaignID) {
        String query = "SELECT COUNT(*) FROM campaign_request WHERE playerID = ? AND campaignID = ?"; //SELECT COUNT restituisce il numero di righe che corrispondono alla condizione
        try (Connection conn = SingletonDBSession.getInstance().startConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setInt(1, userID);
            ps.setInt(2, campaignID);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0; // Ritorna true se trova almeno una riga
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Errore durante il controllo duplicati", e);
        }
        return false;
    }

    public boolean removeRequestOfPartecipation(int userID, int campaignID) {
        try (Connection conn = SingletonDBSession.getInstance().startConnection()) {
            PreparedStatement ps = conn.prepareStatement("DELETE FROM campaign_request WHERE (playerID = ? AND campaignID =? ");
            ps.setInt(1, campaignID);
            ps.setInt(2, userID);
            int rows = ps.executeUpdate();
            if(rows == 0){
                throw new NoRequest("No request present for" + userID + "in" + campaignID);
            }
            return true; // se è falso, vuol dire che non c'era nessuna richiesta presente
        } catch (SQLException _) {
            logger.log(Level.SEVERE, "SQLException occurred while removing request");
            return false; // mettere un'eccezione personalizzata che indichi che il false è dovuto al fatto che non c'è una richiesta
        } finally {
            SingletonDBSession.getInstance().closeConnection();
        }

    }

    //QUESTO METODO POPOLA UNA LISTA, CREANDO O ACCEPTED PLAYERS, WAITING PLAYERS... CONTROLLARE CON LA DAO CAMPAIGN
    public List<User> getPlayersByStatus(int campaignID, String status) {
        List<User> players = new ArrayList<>();
        try (Connection conn = SingletonDBSession.getInstance().startConnection()) {
            PreparedStatement ps = conn.prepareStatement("SELECT u.* FROM user_data u JOIN campaign_request c ON u.userID = c.playerID " +
                    "WHERE c.campaignID ? AND c.status ?");
            ps.setInt(1, campaignID);
            ps.setString(2, status);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    User user = new User();
                    user.setUserID(rs.getInt("userID"));
                    user.setUsername(rs.getString("username"));
                    user.setPassword(rs.getString("password"));
                    UserTypes ustype = UserTypes.valueOf(rs.getString("user_type").toUpperCase());
                    user.setUserType(ustype);
                    // ci sarebbe l'email di google ma per ora tralasciamo
                    players.add(user);
                }
            }
        } catch (SQLException _) {
            logger.log(Level.SEVERE, "SQLException occurred while building list based on status");

        }
        return players;
    }
}
