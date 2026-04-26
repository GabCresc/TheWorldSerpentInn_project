package logic.DAO;

import logic.beans.Bean_Filter;
import logic.model.Model_Campaign;
import logic.utils.SingletonDBSession;
import logic.utils.enums.Mode;
import logic.utils.enums.UserTypes;
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

    public List<Model_Campaign> findCampaignByFilter(Bean_Filter filter) {
        List<Model_Campaign> list_camp = new ArrayList<>();
        //inseriamo i dati in base ai quali l'utente vuole filtrare la ricerca
        try (Connection conn = SingletonDBSession.getInstance().startConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM campaign WHERE " +
                     "(name LIKE ? OR ? IS NULL) AND " +
                     "(time_session = ? OR ? IS NULL) AND " +
                     "(mode = ? OR ? IS NULL)")) {

            // Parametri per il Nome (LIKE viene utilizzato insieme a WHERE per filtrare i records che vogliamo in base a un pattern
            // % rappresenta qualsiasi numero di caratteri (zero incluso). Dunque è utilizzato per la ricerca parziale
            // se l'utente non inserisce nulla, vengono visualizzate tutte le campagne e il filtro ignorato
            ps.setString(1, filter.getNameCampaign() != null ? "%" + filter.getNameCampaign() + "%" : null);
            ps.setString(2, filter.getNameCampaign());

            // Parametri per il Time Session
            ps.setString(3, filter.getTimeSession());
            ps.setString(4, filter.getTimeSession());

            // Parametri per il Mode (
            String modeStr = (filter.getMode() != null) ? filter.getMode().name() : null;
            ps.setString(5, modeStr);
            ps.setString(6, modeStr);

            //Ora estraiamo i risultati della ricerca
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {

                    Model_Campaign mCamp = new Model_Campaign();

                    mCamp.setCampId(rs.getInt("campaignID"));
                    mCamp.setCampName(rs.getString("name"));
                    mCamp.setMaxNumberOfPlayers(rs.getInt("max_players"));
                    mCamp.setCampDMId(rs.getInt("dmID"));
                    mCamp.setTimeSession(rs.getString("time_session"));

                    String modeDb = rs.getString("mode");
//                    if (modeDb != null) {
//                        mCamp.setCampMode(Mode.valueOf(modeDb));
//                    } teoricamente mode non può essere nulla
                    mCamp.setCampMode(Mode.valueOf(modeDb));

                    mCamp.setCampDay(rs.getString("day"));
                    mCamp.setCampCity(rs.getString("city"), mCamp.getCampMode());

                    list_camp.add(mCamp);
                }
            }
        } catch (SQLException e) {

            log.log(Level.SEVERE, "SQLException occurred while filtering campaigns", e);
        }

        return list_camp;
    }

    public boolean removeRequestOfPartecipation(int userID, int campaignID) {
        try (Connection conn = SingletonDBSession.getInstance().startConnection()) {
            PreparedStatement ps = conn.prepareStatement("DELETE FROM campaign_request WHERE (playerID = ? AND campaignID =? ");
            ps.setInt(1, campaignID);
            ps.setInt(2, userID);
            int rows = ps.executeUpdate();
            return rows > 0; // se è falso, vuol dire che non c'era nessuna richiesta presente
        } catch (SQLException e) {
            log.log(Level.SEVERE, "SQLException occurred while removing request");
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
                    user.setID(rs.getInt("userID"));
                    user.setUsername(rs.getString("username"));
                    user.setPassword(rs.getString("password"));
                    UserTypes ustype = UserTypes.valueOf(rs.getString("user_type").toUpperCase());
                    user.setUserType(ustype);
                    // ci sarebbe l'email di google ma per ora tralasciamo
                    players.add(user);
                }
            }
        } catch (SQLException e) {
            log.log(Level.SEVERE, "SQLException occurred while building list based on status");

        }
        return players;
    }
}
