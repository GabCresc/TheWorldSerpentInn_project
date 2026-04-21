package logic.DAO;

import logic.model.User;
import logic.utils.SingletonDBSession;
import java.sql.SQLException;
import logic.utils.enums.UserTypes;

import java.sql.*;

//RICORDA CHE DEVI INSERIRE IL LOGIN CON GOOGLE E CAPIRE COME FARE

// populate connplayers e conndm che crea una hash map?



public class UserDAO {

    public User verifyLogin(String username, String password, boolean isGoogleLogin) throws SQLException {
        User user = null; // restuito nel caso l'utente non esistesse/i parametri fossero errati -> gestione dal controller

        if(!isGoogleLogin) {
            // Accesso senza Google
            try (Connection conn = SingletonDBSession.getInstance().startConnection()) {
                PreparedStatement pstatement = conn.prepareStatement("SELECT userID, username, password, user_type FROM user_data WHERE username = ? AND password = ?");
                pstatement.setString(1, username);
                pstatement.setString(2, password);
                user = getLoggedUser(pstatement);

            } finally {
                SingletonDBSession.getInstance().closeConnection();
            }
        }else{
            // Accesso con Google
            try (Connection conn = SingletonDBSession.getInstance().startConnection()) {
                PreparedStatement pstatement = conn.prepareStatement("SELECT userID, username, password, user_type FROM user_data WHERE username = ? AND password = ?");
                pstatement.setString(1, username);
                user = getLoggedUser(pstatement);
            }catch(SQLException e){e.printStackTrace();
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
                user.setID(rs.getInt("userID"));
                user.setUsername(rs.getString("username"));
                String UserTypSt = rs.getString("user_type").toUpperCase();
                UserTypes.valueOf(UserTypSt);
            } else {
                System.out.println("User does not exist"); //  teoricamente a questo punto dovrebbe essere invocata la registrazione
            }
        } catch (SQLException e) {
            System.err.println("Errore durante l'esecuzione della query: " + e.getMessage());
            e.printStackTrace();
        }
        return user;
    }
    public int getUserIDbyUsername(int user_id, String username) throws SQLException {

        try (Connection conn = SingletonDBSession.getInstance().startConnection()) {
            PreparedStatement pstat = conn.prepareStatement("SELECT userID FROM user_data WHERE username = ? ");
            pstat.setString(1, username); //siamo sicuri sia 1?
            try (ResultSet rs = pstat.executeQuery()) {
                if (rs.next()) {
                    user_id = rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            SingletonDBSession.getInstance().closeConnection();
        }
        return user_id;
    }

    //metodo per registrare l'utente
public void registerUser(User user) throws SQLException{
        try(Connection conn = SingletonDBSession.getInstance().startConnection()) {
            PreparedStatement pstat = conn.prepareStatement("INSERT INTO user_data (userID, username, password, user_type) VALUES(NULL, ?, ?, ?)");
            pstat.setString(1, user.getUsername());
            pstat.setString(2, user.getPassword());
            pstat.setString(3, user.getUsertype().toString()); //nel database è salvato come ENUM, potrebbe dare problemi
            pstat.execute();
        }catch (SQLException e){
            //qualcosa. C'è il caso in cui l'username sia già stato preso

        }finally{
            SingletonDBSession.getInstance().closeConnection();
        }
}
}
