package logic.dao;

import logic.model.User;
import logic.utils.SingletonDBSession;
import java.sql.SQLException;
import logic.utils.enums.UserTypes;
import java.util.logging.Level; // suggerito da SonarCloud
import java.util.logging.Logger;

import java.sql.*;

public class UserJDBC implements UserDAO {

    private static final Logger logger = Logger.getLogger(UserJDBC.class.getName());
    private static final String USERID = "userID";
    private static final String USERNAME = "username";
    private static final String EMAIL = "email";
    private static final String TYPE = "user_type";

    @Override
    public User verifyLogin(String identifier, String password, boolean isGoogleLogin){
        User user = null; // restuito nel caso l'utente non esistesse/i parametri fossero errati -> gestione dal controller
        // nel caso di Google l'identifier è email, nel caso di login standard è username
        if(!isGoogleLogin) {
            // Accesso senza Google
            String query = "SELECT userID, username, password, user_type, email FROM user_data WHERE (BINARY username = ? OR BINARY email = ?)  AND BINARY password = ?";
            try (Connection conn = SingletonDBSession.getInstance().startConnection(); PreparedStatement pstatement = conn.prepareStatement(query)) {

                pstatement.setString(1, identifier);
                pstatement.setString(2, identifier);
                pstatement.setString(3, password);
                user = getLoggedUser(pstatement);
            }catch(SQLException e){
                logger.log(Level.SEVERE, "SQLException occurred while verifying login", e);
            }
        }else{
            // Accesso con Google (password non necessaria)
            String query = "SELECT userID, username, password, user_type, email FROM user_data WHERE BINARY email = ? ";
            try (Connection conn = SingletonDBSession.getInstance().startConnection(); PreparedStatement pstatement = conn.prepareStatement(query)) {

                pstatement.setString(1, identifier);
                user = getLoggedUser(pstatement);
            }catch(SQLException e){
                logger.log(Level.SEVERE, "SQLException occurred while verifying login with google", e);
            }

        }

        return user;
    }

    @Override
    // questo metodo popola i campi di user nel qual caso l'utente fosse già registrato... se non lo è, restituisce null
    public User getLoggedUser(PreparedStatement pstatement) {
        User user = null;
        try (ResultSet rs = pstatement.executeQuery()) {
            if (rs.next()) {
                user = new User();
                user.setUserID(rs.getInt(USERID));
                user.setUsername(rs.getString(USERNAME));
                user.setPassword(rs.getString("password"));
                user.setEmail(rs.getString(EMAIL));

                String typeStr = rs.getString(TYPE);
                if(typeStr!=null){
                    UserTypes type = UserTypes.valueOf(typeStr);
                    user.setUserType(type);
                }else{
                    user.setUserType(null);
                }
            } else {
                logger.log(Level.INFO, "User does not exist");
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "SQLException occurred while getting LoggedUser", e);
        }
        return user;
    }

    @Override
    public int getUserIDbyUsername(String username) {
        int userid = 0;
        String query = "SELECT userID FROM user_data WHERE username = ? ";
        try (Connection conn = SingletonDBSession.getInstance().startConnection(); PreparedStatement pstat = conn.prepareStatement(query)) {

            pstat.setString(1, username);
            try (ResultSet rs = pstat.executeQuery()) {
                if (rs.next()) {
                    userid = rs.getInt(USERID);
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "SQLException occurred while retrieving User_ID with Username", e);
        }
        return userid;
    }

    @Override
    public String getUsernameByUserId(int userID) {
        String username = null;
        String query = "SELECT username FROM user_data WHERE userID = ? ";
        try (Connection conn = SingletonDBSession.getInstance().startConnection();  PreparedStatement pstat = conn.prepareStatement(query)) {
            pstat.setInt(1, userID);
            try (ResultSet rs = pstat.executeQuery()) {
                if (rs.next()) {
                    username = rs.getString(USERNAME);
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "SQLException occurred while retrieving Username with User_ID", e);
        }
        return username;
    }

    @Override
    //metodo per registrare l'utente
    public void registerUser(User user){
        String query = "INSERT INTO user_data (userID, username, password, user_type, email) VALUES(NULL, ?, ?, ?, ?)";
        try(Connection conn = SingletonDBSession.getInstance().startConnection(); PreparedStatement pstat = conn.prepareStatement(query)) {

            pstat.setString(1, user.getUsername());

            //ulteriore precauzione per evitare problemi nel database
            if (user.getPassword() == null || user.getPassword().isEmpty()) {
                pstat.setNull(2, Types.VARCHAR);
            } else {
                pstat.setString(2, user.getPassword());
            }

            if (user.getUserType() == null) {
                pstat.setNull(3, Types.VARCHAR);
            } else {
                pstat.setString(3, user.getUserType().toString());
            }

            pstat.setString(4, user.getEmail());

            pstat.execute();
        }catch (SQLException e){
            logger.log(Level.SEVERE, "SQLException occurred while registering user", e);
        }
    }

    @Override
    //controlliamo se l'utente esiste tramite username/email
    public boolean existenceUser(String identifier){
        boolean result = true;
        String query = "SELECT userID FROM user_data WHERE username = ? OR email = ?";
        try(Connection conn = SingletonDBSession.getInstance().startConnection(); PreparedStatement ps = conn.prepareStatement(query)){

            ps.setString(1, identifier);
            ps.setString(2, identifier);
            try(ResultSet rs = ps.executeQuery()){
                result = rs.next();
            }
            return result;
        }catch(SQLException e){
            logger.log(Level.SEVERE, "SQLException occurred while verifying user existence", e);
        }
        return !result;
    }

    @Override
    public User retrieveUserByUsername(String username) {
        User user = null;
        String query = "SELECT userID, username, user_type, email  FROM user_data WHERE username = ?";
        SingletonDBSession session = SingletonDBSession.getInstance();
        try (Connection conn = session.startConnection();  PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                user = mapResult(rs);
            }
            return user;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "SQLException occurred while retrieving player from db", e);
            return null;
        }
    }

    @Override
    public User retrieveUserByUserID(int userID){
        User user = null;
        String query = "SELECT userID, username, user_type, email FROM user_data WHERE userID = ?";
        SingletonDBSession session = SingletonDBSession.getInstance();
        try(Connection conn = session.startConnection(); PreparedStatement ps = conn.prepareStatement(query)){

            ps.setInt(1, userID);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                user = mapResult(rs);
            }
            return user;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "SQLException occurred while retrieving player from db", e);
            return null;
        }

        }
        public User mapResult(ResultSet rs) throws SQLException{
            User user = new User();
            user.setUsername(rs.getString(USERNAME));
            user.setUserID(rs.getInt(USERID));
            user.setEmail(rs.getString(EMAIL));

            String typeString = rs.getString(TYPE);
            if (typeString != null) {
                user.setUserType(UserTypes.valueOf(typeString.toUpperCase()));
            }
            return user;
        }
    }






