package logic.DAO;

import logic.beans.Bean_Filter;
import logic.model.Model_Campaign;
import logic.utils.SingletonDBSession;
import java.sql.SQLException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import logic.utils.enums.Mode;
import lombok.extern.java.Log;
import java.util.logging.Level; // suggerito da SonarCloud



// VERIFICARE CHE I FINALLY SIANO EFFETTIVAMENTE NECESSARI
@Log
public class CampaignDAO {

    // apro la connessione con il database per recuperare le campagne disponibili
    public List<Model_Campaign> retrieveCampaigns() {
        List<Model_Campaign> list = new ArrayList<>();

        try(Connection conn = SingletonDBSession.getInstance().startConnection()){
            Statement statement = conn.createStatement();
            ResultSet rs = statement.executeQuery("SELECT campaign.*, COUNT(campaign_request.playerID) as current_players, " +
                    "FROM campaign LEFT JOIN campaign_request ON campaign.campaignID = campaign_request.campaignID AND campaign_request.status = 'ACCEPTED' " +
                    "GROUP BY campaign.campaignID");

            // Con questa query andiamo a selezionare tutte le colonne di campaign, contiamo il numero di righe associate a PlayerID (che chiamiamo current_players
            // dopodiché prendiamo tutte le colonne di campaign (LEFT) che uniamo a quelle di campaign_request che hanno lo stesso campaignID e che hanno come status
            // ACCEPTED. Infine raggruppiamo per campaignID

                while(rs.next()){
                    int max_players = rs.getInt("max_players");
                    int current_players = rs.getInt("current_players");

                    if(max_players > current_players) {

                        Model_Campaign camp = new Model_Campaign();
                        camp.setCampId(rs.getInt("idCampaign"));
                        camp.setCampName(rs.getString("name"));
                        camp.setMaxNumberOfPlayers(rs.getInt("max_players"));
                        camp.setDmId(rs.getInt("dm_id"));
                        camp.setTimeSession(rs.getString("time_session"));
                        Mode mode = Mode.valueOf(rs.getString("mode").toUpperCase());
                        camp.setCampMode(mode);
                        camp.setCampDay(rs.getString("day"));
                        camp.setCampCity(rs.getString("city"), mode);
                        list.add(camp);

                    }
                }
            } catch (SQLException e){ log.log(Level.SEVERE, "SQLException occurred while retrieving campaigns"); }
        finally {
            SingletonDBSession.getInstance().closeConnection();
        }
            return list;
        }

// metodo per registrare la richiesta del player nel database
    public void insertRequest(int campaignID, int playerID){
            try(Connection conn = SingletonDBSession.getInstance().startConnection()){
                PreparedStatement pstatement = conn.prepareStatement("INSERT INTO campaign_request (campaignID, playerID, status) VALUES (?,?, 'WAITING')");
                pstatement.setInt(1, campaignID); //1 indica l'indice di colonna
                pstatement.setInt(2, playerID);
                pstatement.executeUpdate();
                // executeUpdate() è un metodo della classe Statement utilizzato per eseguire istruzioni DML (INSERT, UPDATE, DELETE) o DDL,
                // e restituisce un intero che indica il numero di righe interessate dalla modifica
            }catch (SQLException e) { log.log(Level.SEVERE, "SQLException occurred while inserting requests"); }

            // con solo e, viene stampato tutto lo stacktrace, mentre con e.getmessage() solo il messaggio. Quando usarlo?
        //e.getmessage() utile se il msg arriva all'utente finale
    }


    public List<Integer> retrieveListofWaitingPlayers (int campaignID) {
        List<Integer> list_id = new ArrayList<>();

        try (Connection conn = SingletonDBSession.getInstance().startConnection()) {
            PreparedStatement pstatement = conn.prepareStatement("SELECT playerID FROM campaign_request WHERE playerID = ? and status = 'WAITING' ");
            // WHERE viene utilizzata per selezionare specifici record che soddisfano una certa condizione
            // ? rappresenta un placeholder poi sostituito dal valore specifico
            pstatement.setInt(1, campaignID);
            ResultSet rs = pstatement.executeQuery();
            // utilizziamo executeQuery() senza parametri perché stiamo eseguendo o una procedura registrata che restituisce un singolo result set
            // oppure per runnare SELECT queries che non richiedono valori di input dinamici DA CONTROLLARE
            while (rs.next()) {
                list_id.add(rs.getInt("playerID"));
            }
        } catch (SQLException e) {
            log.log(Level.SEVERE, "SQLException occurred while retrieving list of waiting players");
        }finally {
            SingletonDBSession.getInstance().closeConnection();
        }
        return list_id;
    }

    public List<Model_Campaign> findCampaignByFilter(Bean_Filter filter) {
        List<Model_Campaign> list_camp = new ArrayList<>();
        //inseriamo i dati in base ai quali l'utente vuole filtrare la ricerca
        try (Connection conn = SingletonDBSession.getInstance().startConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM campaign WHERE " +
                     "(name LIKE ? OR ? IS NULL) AND " +
                     "(time_session = ? OR ? IS NULL) AND " +
                     "(mode = ? OR ? IS NULL)")) {

            // LIKE viene utilizzato insieme a WHERE per filtrare i records che vogliamo in base a un pattern
            // % rappresenta qualsiasi numero di caratteri (zero incluso). Dunque è utilizzato per la ricerca parziale
            // se l'utente non inserisce nulla, vengono visualizzate tutte le campagne e il filtro ignorato
            
            ps.setString(1, filter.getNameCampaign() != null ? "%" + filter.getNameCampaign() + "%" : null);
            ps.setString(2, filter.getNameCampaign());

            
            ps.setString(3, filter.getTimeSession());
            ps.setString(4, filter.getTimeSession());

            
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


}


