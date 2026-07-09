package logic.dao;

import logic.model.ModelCampaign;
import logic.utils.SingletonDBSession;
import java.sql.SQLException;
import logic.beans.BeanFilter;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import logic.utils.enums.Mode;

import java.util.logging.Level; // suggerito da SonarCloud
import java.util.logging.Logger;

public class CampaignJDBC implements CampaignDAO {

    private static final Logger logger = Logger.getLogger(CampaignJDBC.class.getName());

    @Override
    // apro la connessione con il database per recuperare le campagne disponibili
    public List<ModelCampaign> retrieveCampaigns() { //ok
        List<ModelCampaign> list = new ArrayList<>();
        String query = "SELECT campaign.*, COUNT(campaign_request.playerID) as currentPlayers " +
                "FROM campaign LEFT JOIN campaign_request ON campaign.campaignID = campaign_request.campaignID AND campaign_request.status = 'ACCEPTED' " +
                "GROUP BY campaign.campaignID";
        try (Connection conn = SingletonDBSession.getInstance().startConnection(); Statement statement = conn.createStatement();ResultSet rs = statement.executeQuery(query);) {

            // Con questa query andiamo a selezionare tutte le colonne di campaign, contiamo il numero di righe associate a PlayerID (che chiamiamo current_players
            // dopodiché prendiamo tutte le colonne di campaign (LEFT) che uniamo a quelle di campaign_request che hanno lo stesso campaignID e che hanno come status
            // ACCEPTED. Infine raggruppiamo per campaignID

            while (rs.next()) {
                int maxPlayers = rs.getInt("max_players");
                int currentPlayers = rs.getInt("currentPlayers");

                if (maxPlayers > currentPlayers) {

                    ModelCampaign camp = mapResult(rs);
                    list.add(camp);

                }
            }

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Exception occurred while retrieving campaigns", e);
        }
        return list;
    }

    @Override
    public boolean addCampaign(ModelCampaign camp){ //ok
        String query = "INSERT INTO campaign (name, max_players, userID, time_session, mode, city, " +
                "start_date, frequency, platform) VALUES (?,?,?,?,?,?,?,?,?)";
        try(Connection conn = SingletonDBSession.getInstance().startConnection(); PreparedStatement ps = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)){

            ps.setString(1, camp.getCampName());
            ps.setInt(2, camp.getMaxPlayers());
            ps.setInt(3, camp.getCampDmId());
            ps.setTime(4, Time.valueOf(camp.getCampTimeSession()));
            ps.setString(5, camp.getCampMode().toString());
            ps.setString(6, camp.getCampCity());
            Timestamp ts = Timestamp.valueOf(camp.getCampStartDate());
            ps.setTimestamp(7, ts);
            ps.setString(8, camp.getCampFreq());
            ps.setString(9, camp.getPlatform());
            int rows = ps.executeUpdate();
            if (rows > 0) {
                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        // aggiorniamo l'ID generato dal database
                        camp.setCampId(generatedKeys.getInt(1));
                    }
                }
            }
            return rows > 0;
        }catch(SQLException e){
            logger.log(Level.SEVERE, "SQLException occurred while adding notification to JDBC", e);
            return false;
        }
    }


    @Override
    public void deleteCampaign(int campaignID){ //ok
        String query = "DELETE FROM campaign WHERE campaignID = ?";
        try(Connection conn = SingletonDBSession.getInstance().startConnection(); PreparedStatement ps = conn.prepareStatement(query)){

            ps.setInt(1, campaignID);
            ps.executeUpdate();
        }catch(SQLException e){
            logger.log(Level.SEVERE, "SQLException occurred while deleting campaign", e);
        }
    }

    @Override
    public List<ModelCampaign> findCampaignByFilter(BeanFilter filter) { //ok
        List<ModelCampaign> listCamp = new ArrayList<>();
        //inseriamo i dati in base ai quali l'utente vuole filtrare la ricerca
        SingletonDBSession session = SingletonDBSession.getInstance();
        try (Connection conn = session.startConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM campaign WHERE " +
                     "(name LIKE ? OR ? IS NULL) AND " +
                     "(mode = ? OR ? IS NULL)")) {

            // LIKE viene utilizzato insieme a WHERE per filtrare i records che vogliamo in base a un pattern
            // % rappresenta qualsiasi numero di caratteri (zero incluso). Dunque è utilizzato per la ricerca parziale
            // se l'utente non inserisce nulla, vengono visualizzate tutte le campagne e il filtro ignorato

            ps.setString(1, filter.getNameCampaign() != null ? "%" + filter.getNameCampaign() + "%" : null);
            ps.setString(2, filter.getNameCampaign());

            String modeStr = (filter.getMode() != null) ? filter.getMode().name() : null;
            ps.setString(3, modeStr);
            ps.setString(4, modeStr);


            //Ora estraiamo i risultati della ricerca
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {

                    ModelCampaign mCamp = mapResult(rs);

                    listCamp.add(mCamp);
                }
            }
        } catch (SQLException e) {

            logger.log(Level.SEVERE, "Exception occurred while filtering campaigns", e);
        }

        return listCamp;
    }

    @Override
    public ModelCampaign getCampaignById(int campaignID){ //ok
        String query ="SELECT campaignID, name, max_players, userID, time_session, mode, city, start_date, frequency, platform FROM campaign WHERE campaignID = ?";
        try(Connection conn = SingletonDBSession.getInstance().startConnection(); PreparedStatement ps = conn.prepareStatement(query)){
            ps.setInt(1, campaignID);
            try(ResultSet rs = ps.executeQuery()){
                if(rs.next()){
                 return mapResult(rs);
                }
            }
        }catch(SQLException e){
            logger.log(Level.SEVERE, "Exception occurred while retrieving campaign by campaignID", e);
        }

        return null;
    }


    private ModelCampaign mapResult(ResultSet rs) throws SQLException { //ok
        ModelCampaign camp = new ModelCampaign();
        camp.setCampId(rs.getInt("campaignID"));
        camp.setCampName(rs.getString("name"));
        camp.setMaxNumberOfPlayers(rs.getInt("max_players"));
        camp.setCampDmId(rs.getInt("userID"));
        Mode mode = Mode.valueOf(rs.getString("mode").toUpperCase());
        camp.setCampMode(mode);
        camp.setCampCity(rs.getString("city"));
        camp.setCampFreq(rs.getString("frequency"));
        Timestamp ts = rs.getTimestamp("start_date");
        camp.setPlatform(rs.getString("platform"));
        if(ts != null) camp.setCampStartDate(ts.toLocalDateTime());

        Time timeS = rs.getTime("time_session");
        if(timeS != null) camp.setTimeSession(timeS.toLocalTime());


        return camp;
    }

}



