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

public class ParticipationJDBC implements ParticipationDAO {

    private static final Logger logger = Logger.getLogger(ParticipationJDBC.class.getName());

    @Override
    // questo metodo aggiunge un player nella lista dei waiting players
    public void addWaitingPlayer(int userID, int campaignID) throws RequestAlreadySent { //ok
        if(isRequestAlreadyPresent(userID, campaignID)){
            throw new RequestAlreadySent("Request already sent to this campaign!");
        }
        String query = "INSERT INTO campaign_request (campaignID, playerID, status) VALUES (?,?,?)";
        try (Connection conn = SingletonDBSession.getInstance().startConnection(); PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, campaignID);
            ps.setInt(2, userID);
            ps.setString(3, Status.WAITING.toString());

            ps.executeUpdate();
        } catch (SQLException _) {
            logger.log(Level.SEVERE, "SQLException occurred while adding player to the waiting list");
        }
    }

    @Override
    public boolean isRequestAlreadyPresent(int userID, int campaignID) { //ok
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
    @Override
    public boolean removeRequestOfParticipation(int userID, int campaignID) {//ok
        String query = "DELETE FROM campaign_request WHERE (playerID = ? AND campaignID =?)";
        try (Connection conn = SingletonDBSession.getInstance().startConnection(); PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(2, campaignID);
            ps.setInt(1, userID);
            int rows = ps.executeUpdate();
            if (rows == 0) {
                throw new NoRequest("No request present for" + userID + "in" + campaignID);
            }
            return true; // se è falso, vuol dire che non c'era nessuna richiesta presente
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "SQLException occurred while removing request", e);
            return false;
        }
    }

    @Override
    //QUESTO METODO POPOLA UNA LISTA, CREANDO O ACCEPTED PLAYERS, WAITING PLAYERS
    public List<User> getPlayersByStatus(int campaignID, String status) { //ok
        List<User> players = new ArrayList<>();
        String query = "SELECT u.* FROM user_data u JOIN campaign_request c ON u.userID = c.playerID WHERE c.campaignID = ? AND c.status = ?";
        try (Connection conn = SingletonDBSession.getInstance().startConnection(); PreparedStatement ps = conn.prepareStatement(query)) {
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
                    user.setEmail("email");
                    players.add(user);
                }
                return players;
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "SQLException occurred while building list based on status", e);

        }
        return players;
    }

    @Override
   public boolean acceptPlayer(int userID, int campaignID, Status status1){
        String query = "UPDATE campaign_request SET status = ? WHERE (playerID = ? AND campaignID = ?)";
        try(Connection conn = SingletonDBSession.getInstance().startConnection();PreparedStatement ps = conn.prepareStatement(query) ){
            ps.setString(1, status1.toString());
            ps.setInt(2, userID);
            ps.setInt(3, campaignID);
             int rows = ps.executeUpdate();
             return rows > 0;

        }catch(SQLException e){
            logger.log(Level.SEVERE, "SQLException occurred while changing participation request status", e);
        }
        return false;
    }
}
