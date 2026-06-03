package logic.dao;

import logic.model.User;
import logic.utils.SingletonDBSession;
import java.sql.SQLException;
import logic.utils.enums.UserTypes;
import java.util.logging.Level; // suggerito da SonarCloud
import java.util.logging.Logger;

import java.sql.*;

// populate connplayers e conndm che crea una hash map?


public class UserDAO {

    private static final Logger logger = Logger.getLogger(UserDAO.class.getName());

    public User verifyLogin(String identifier, String password, boolean isGoogleLogin) {
        User user = null; // restuito nel caso l'utente non esistesse/i parametri fossero errati -> gestione dal controller
        // nel caso di Google l'identifier è email, nel caso di login standard è username
        if(!isGoogleLogin) {
            // Accesso senza Google
            try (Connection conn = SingletonDBSession.getInstance().startConnection()) {
                PreparedStatement pstatement = conn.prepareStatement("SELECT userID, username, password, user_type, email FROM user_data WHERE username = ? AND password = ?");
                pstatement.setString(1, identifier);
                pstatement.setString(2, password);
                user = getLoggedUser(pstatement);
            }catch(SQLException _){
                logger.log(Level.SEVERE, "SQLException occurred while verifying login");
            } finally {
                SingletonDBSession.getInstance().closeConnection();
            }
        }else{
            // Accesso con Google (password non necessaria)
            try (Connection conn = SingletonDBSession.getInstance().startConnection()) {
                PreparedStatement pstatement = conn.prepareStatement("SELECT userID, username, user_type FROM user_data WHERE email = ? ");
                pstatement.setString(1, identifier);
                user = getLoggedUser(pstatement);
            }catch(SQLException _){ logger.log(Level.SEVERE, "SQLException occurred while veryfing login with Google");
            }finally{
                SingletonDBSession.getInstance().closeConnection();
            }

        }

        return user;
    }

    // questo metodo popola i campi di user nel qual caso l'utente fosse già registrato... se non lo è, restituisce null
    public User getLoggedUser(PreparedStatement pstatement) {
       User user = null;
        try (ResultSet rs = pstatement.executeQuery()) {
            if (rs.next()) {
                user = new User();
                user.setUserID(rs.getInt("userID"));
                user.setUsername(rs.getString("username"));

                String typeString = rs.getString("user_type").toUpperCase();
                UserTypes type = UserTypes.valueOf(typeString);

                if(type == UserTypes.PLAYER){
                    user.setUserType(UserTypes.PLAYER);
                }else{
                    user.setUserType(UserTypes.DM);
                }

            } else {
                logger.log(Level.INFO, "User does not exist");
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "SQLException occurred while getting LoggedUser" + e.getMessage());
        }
        return user;
    }

    public int getUserIDbyUsername(String username) {
        int userid = 0;
        try (Connection conn = SingletonDBSession.getInstance().startConnection()) {
            PreparedStatement pstat = conn.prepareStatement("SELECT userID FROM user_data WHERE username = ? ");
            pstat.setString(1, username); //siamo sicuri sia 1?
            try (ResultSet rs = pstat.executeQuery()) {
                if (rs.next()) {
                    userid = rs.getInt("userID");
                }
            }
        } catch (SQLException _) {
            logger.log(Level.SEVERE, "SQLException occurred while retrieving User_ID with Username");
        } finally {
            SingletonDBSession.getInstance().closeConnection();
        }
        return userid;
    }

    public String getUsernameByUserId(int userID) {
        String username = null;
        try (Connection conn = SingletonDBSession.getInstance().startConnection()) {
            PreparedStatement pstat = conn.prepareStatement("SELECT username FROM user_data WHERE userID = ? ");
            pstat.setInt(1, userID); //siamo sicuri sia 1?
            try (ResultSet rs = pstat.executeQuery()) {
                if (rs.next()) {
                    username = rs.getString("username");
                }
            }
        } catch (SQLException _) {
            logger.log(Level.SEVERE, "SQLException occurred while retrieving Username with User_ID");
        } finally {
            SingletonDBSession.getInstance().closeConnection();
        }
        return username;
    }

    //metodo per registrare l'utente
public void registerUser(User user){
        try(Connection conn = SingletonDBSession.getInstance().startConnection()) {
            PreparedStatement pstat = conn.prepareStatement("INSERT INTO user_data (userID, username, password, user_type, email) VALUES(NULL, ?, ?, ?, ?)");
            pstat.setString(1, user.getUsername());
            pstat.setString(4, user.getEmail());
            pstat.setString(3, user.getUserType().toString());//nel database è salvato come ENUM, potrebbe dare problemi
            if(user.getPassword() == null || user.getPassword().isEmpty()){
                pstat.setNull(2, Types.VARCHAR);
            }else{
                pstat.setString(2, user.getPassword());
            }
            pstat.execute();
        }catch (SQLException _){
           /* if(e.getErrorCode() == 1111){ // il codice non è giusto, devo capire come ottenerlo
                throw new UsernameTaken(e.getMessage(), user.getUsername());
            }*/
            logger.log(Level.SEVERE, "SQLException occurred while registering user");
        }finally{
            SingletonDBSession.getInstance().closeConnection();
        }
    }

    public Boolean existenceUser(String username){
        boolean result = false;
        try(Connection conn = SingletonDBSession.getInstance().startConnection()){
            PreparedStatement ps = conn.prepareStatement("SELECT userID FROM user_data WHERE username = ? ");
            ps.setString(1, username);
            try(ResultSet rs = ps.executeQuery()){
                result = rs.next();
            }
        }catch(SQLException e){
            logger.log(Level.SEVERE, "SQLException occurred while verifying user existence");
        }finally{
            SingletonDBSession.getInstance().closeConnection();
        }
        return !result;
    }
}





