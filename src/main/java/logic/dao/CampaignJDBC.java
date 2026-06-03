package logic.dao;

import logic.model.ModelCampaign;
import logic.utils.SingletonDBSession;
import java.sql.SQLException;
import logic.beans.BeanFilter;

import java.sql.*;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import logic.utils.enums.Mode;
import java.util.logging.Level; // suggerito da SonarCloud
import java.util.logging.Logger;


// VERIFICARE CHE I FINALLY SIANO EFFETTIVAMENTE NECESSARI (nel caso di try with resources, i finally
// eseguono blocchi di codice dopo che le risorse sono state chiuse)
// Come scritto su Oracle, è preferibile utilizzare il try with resources, poiché nel caso in cui il programma
// si interrompesse in modo improvviso prima dell'esecuzione di finally, la risorsa andrebbe perduta)

public class CampaignDAO {

    private static final Logger logger = Logger.getLogger(CampaignDAO.class.getName());

    // apro la connessione con il database per recuperare le campagne disponibili
    public List<ModelCampaign> retrieveCampaigns() { //DA METTERE A POSTO
        List<ModelCampaign> list = new ArrayList<>();
        SingletonDBSession session = SingletonDBSession.getInstance();
        try(Connection conn = session.startConnection()){
            Statement statement = conn.createStatement();
            ResultSet rs = statement.executeQuery("SELECT campaign.*, COUNT(campaign_request.playerID) as currentPlayers, " +
                    "FROM campaign LEFT JOIN campaign_request ON campaign.campaignID = campaign_request.campaignID AND campaign_request.status = 'ACCEPTED' " +
                    "GROUP BY campaign.campaignID");

            // Con questa query andiamo a selezionare tutte le colonne di campaign, contiamo il numero di righe associate a PlayerID (che chiamiamo current_players
            // dopodiché prendiamo tutte le colonne di campaign (LEFT) che uniamo a quelle di campaign_request che hanno lo stesso campaignID e che hanno come status
            // ACCEPTED. Infine raggruppiamo per campaignID

            while(rs.next()){
                int maxPlayers = rs.getInt("max_players");
                int currentPlayers = rs.getInt("currentPlayers");

                if(maxPlayers > currentPlayers) {

                    ModelCampaign camp = mapResult(rs);
                    list.add(camp);

                }
            }
        } catch (SQLException _){ logger.log(Level.SEVERE, "SQLException occurred while retrieving campaigns"); }
        return list;
    }

    // metodo per registrare la richiesta del player nel database
    public void insertRequest(int campaignID, int playerID){
        SingletonDBSession session = SingletonDBSession.getInstance();
        try(Connection conn = session.startConnection()){
            PreparedStatement pstatement = conn.prepareStatement("INSERT INTO campaign_request (campaignID, playerID, status) VALUES (?,?, 'WAITING')");
            pstatement.setInt(1, campaignID); //1 indica l'indice di colonna
            pstatement.setInt(2, playerID);
            pstatement.executeUpdate();
            // executeUpdate() è un metodo della classe Statement utilizzato per eseguire istruzioni DML (INSERT, UPDATE, DELETE) o DDL,
            // e restituisce un intero che indica il numero di righe interessate dalla modifica
        }catch (SQLException _) { logger.log(Level.SEVERE, "SQLException occurred while inserting requests"); }

        // con solo e, viene stampato tutto lo stacktrace, mentre con e.getmessage() solo il messaggio. Quando usarlo?
        //e.getmessage() utile se il msg arriva all'utente finale
    }


    public List<Integer> retrieveListofWaitingPlayers (int campaignID) {
        List<Integer> listId = new ArrayList<>();
        SingletonDBSession session = SingletonDBSession.getInstance();
        try (Connection conn = session.startConnection()) {
            PreparedStatement pstatement = conn.prepareStatement("SELECT playerID FROM campaign_request WHERE campaignID = ? and status = 'WAITING' ");
            // WHERE viene utilizzata per selezionare specifici record che soddisfano una certa condizione
            // ? rappresenta un placeholder poi sostituito dal valore specifico
            pstatement.setInt(1, campaignID);
            ResultSet rs = pstatement.executeQuery();
            // utilizziamo executeQuery() senza parametri perché stiamo eseguendo o una procedura registrata che restituisce un singolo result set
            // oppure per runnare SELECT queries che non richiedono valori di input dinamici DA CONTROLLARE
            while (rs.next()) {
                listId.add(rs.getInt("playerID"));
            }
        } catch (SQLException _) {
            logger.log(Level.SEVERE, "SQLException occurred while retrieving list of waiting players");
        }finally {
            SingletonDBSession.getInstance().closeConnection();
        }
        return listId;
    }

    public List<ModelCampaign> findCampaignByFilter(BeanFilter filter) {
        List<ModelCampaign> listCamp = new ArrayList<>();
        //inseriamo i dati in base ai quali l'utente vuole filtrare la ricerca
        SingletonDBSession session = SingletonDBSession.getInstance();
        try (Connection conn = session.startConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM campaign WHERE " +
                     "(name LIKE ? OR ? IS NULL) AND " +
                     "(time_session = ? OR ? IS NULL) AND " +
                     "(mode = ? OR ? IS NULL)")) {

            // LIKE viene utilizzato insieme a WHERE per filtrare i records che vogliamo in base a un pattern
            // % rappresenta qualsiasi numero di caratteri (zero incluso). Dunque è utilizzato per la ricerca parziale
            // se l'utente non inserisce nulla, vengono visualizzate tutte le campagne e il filtro ignorato

            ps.setString(1, filter.getNameCampaign() != null ? "%" + filter.getNameCampaign() + "%" : null);
            ps.setString(2, filter.getNameCampaign());

            LocalTime time = filter.getTimeSession();
            if (time != null) {
                ps.setTime(3, Time.valueOf(time));
                ps.setTime(4, Time.valueOf(time));
            } else {
                ps.setNull(3, Types.TIME);
                ps.setNull(4, Types.TIME);
            }

            String modeStr = (filter.getMode() != null) ? filter.getMode().name() : null;
            ps.setString(5, modeStr);
            ps.setString(6, modeStr);

            //Ora estraiamo i risultati della ricerca
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {

                    ModelCampaign mCamp = mapResult(rs);

                    listCamp.add(mCamp);
                }
            }
        } catch (SQLException e) {

            logger.log(Level.SEVERE, "SQLException occurred while filtering campaigns", e);
        }

        return listCamp;
    }

    public ModelCampaign getCampaignById(int campaignID){

        try(Connection conn = SingletonDBSession.getInstance().startConnection()){
            PreparedStatement ps = conn.prepareStatement(("SELECT name, max_players, dmID, time_session, mode, day, city, end_date FROM campaign WHERE campaignID = ?"));
            ps.setInt(1, campaignID);
            try(ResultSet rs = ps.executeQuery()){
                if(rs.next()){
                 return mapResult(rs);
                }
            }
        }catch(SQLException e){
            logger.log(Level.SEVERE, "SQLException occurred while retrievein campaign by campaignID", e);
        }
        return null;
    }

    private ModelCampaign mapResult(ResultSet rs) throws SQLException {
        ModelCampaign camp = new ModelCampaign();
        camp.setCampId(rs.getInt("campaignID"));
        camp.setCampName(rs.getString("name"));
        camp.setMaxNumberOfPlayers(rs.getInt("max_players"));
        camp.setCampDmId(rs.getInt("dmID"));

        Mode mode = Mode.valueOf(rs.getString("mode").toUpperCase());
        camp.setCampMode(mode);
        camp.setCampCity(rs.getString("city"), mode);
        camp.setCampDay(rs.getInt("day"));

        Timestamp ts = rs.getTimestamp("end_date");
        if(ts != null) camp.setCampEndDate(ts.toLocalDateTime());

        Time timeS = rs.getTime("time_session");
        if(timeS != null) camp.setTimeSession(timeS.toLocalTime());

        return camp;
    }
}



