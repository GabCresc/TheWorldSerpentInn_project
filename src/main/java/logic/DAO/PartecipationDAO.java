package logic.DAO;

import logic.utils.SingletonDBSession;
import lombok.extern.java.Log;
import logic.model.User;
import logic.utils.enums.Status;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.sql.*;
import java.util.List;
import java.util.logging.Level; // suggerito da SonarCloud

@Log
public class PartecipationDAO {

    // questo metodo aggiunge un player nella lista dei waiting players
    public void addWaitingPlayer(int userID, int campaignID) {
        try (Connection conn = SingletonDBSession.getInstance().startConnection()) {
            PreparedStatement ps = conn.prepareStatement("INSERT INTO campaign_request (campaignID, playerID, status) VALUES (?,?,?)");
            ps.setInt(1, campaignID);
            ps.setInt(2, userID);
            ps.setString(3, Status.WAITING.name()); // creare un enumeration per lo status dei player?

            ps.executeUpdate();
        } catch (SQLException e) {
            log.log(Level.SEVERE, "SQLException occurred while adding player to the waiting list");
            // INSERIRE UNA PROPRIA ECCEZIONE QUI. Che succede se il player ha giaà inviato la richiesta ed è nella lista?
        }
    }

    public List<Integer> getPlayerIDByStatus(int campaignID, String status) {
        List<Integer> list_playerID = new ArrayList<>();
        try (Connection conn = SingletonDBSession.getInstance().startConnection()) {
            PreparedStatement ps = conn.prepareStatement("SELECT playerID FROM campaign_request WHERE campaignID = ? and status = ?");
            ps.setInt(1, campaignID);
            ps.setString(2, status);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list_playerID.add(rs.getInt("playerID"));
                }
            }

        } catch (SQLException e) {
            log.log(Level.SEVERE, "SQLException while getting list of players by status", e);
        }
        return list_playerID;
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
            log.log(Level.SEVERE, "Errore durante il controllo duplicati", e);
        }
        return false;
    }

}

