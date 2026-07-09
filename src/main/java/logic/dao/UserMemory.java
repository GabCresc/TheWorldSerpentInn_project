package logic.dao;

import logic.model.User;
import logic.utils.enums.UserTypes;

import java.sql.PreparedStatement;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UserMemory implements UserDAO {
    private static final Logger logger = Logger.getLogger(UserMemory.class.getName());

    private static Map<Integer,User> usersById = new HashMap<>();//la chiave è lo userID
    private static Map<String, User> usersByUsername = new HashMap<>();
    private static int counter = 1;

    public UserMemory(){
        if(usersById.isEmpty()){
            loadData();
        }
    }

    //metodo utile per evitare che SonarCloud dia problemi
    private static void privateRegister(User user){
        if(user.getUserID() == 0){
            user.setUserID(counter);
            counter++;
        }
        usersById.put(user.getUserID(), user);
        if(user.getUsername() != null){
            usersByUsername.put(user.getUsername().toLowerCase(), user);
        }

        if(user.getEmail() != null){
            usersByUsername.put(user.getEmail().toLowerCase(), user);
        }
    }


    // inserisci dei dati per la simulazione
    private static void loadData(){ //ok
        User u1 = new User();
        u1.setUserID(counter++);
        u1.setUsername("Kyor");
        u1.setPassword("12345678");
        u1.setEmail("mariorossi@gmail.com");
        u1.setUserType(UserTypes.PLAYER);
        privateRegister(u1);


        User u2 = new User();
        u2.setUserID(counter++);
        u2.setUsername("Yarissa");
        u2.setPassword("4567890123");
        u2.setEmail("luciobianchi@gmail.com");
        u2.setUserType(UserTypes.DM);
        privateRegister(u2);
    }

    @Override
    public User verifyLogin(String identifier, String password, boolean isGoogleLogin){
        User user = usersByUsername.get(identifier.toLowerCase());


        if(user != null){
            if(isGoogleLogin){
                return user;
            }else{ //ok
                if(user.getPassword().equals(password)) {
                    return user;
                }
                }
        }
        logger.log(Level.INFO, "Password or username are wrong or user does not exist.");
        return null;
    }

    @Override
    public void registerUser(User user) {
        privateRegister(user);
    }

    @Override
    public int getUserIDbyUsername(String username){ //ok
        User user = usersByUsername.get(username);
        if( user != null){
            return user.getUserID();
        }else{
            return 0; // no users found
        }
    }

    @Override
    public String getUsernameByUserId(int userID){ //ok
        User user = usersById.get(userID);
        if(user!=null){
            return user.getUsername();
        }else{
            return null;
        }
    }

    @Override
    public User getLoggedUser(PreparedStatement pstatement){ // non necessario qui, ma va implementato
        return null;
    }

    @Override
    public boolean existenceUser(String username){
        User user = usersByUsername.get(username.toLowerCase());
        if(user!=null){
            return true;
        }else{
            logger.log(Level.INFO, "User does not exist");
            return false;
        }
    }

    @Override
    public User retrieveUserByUsername(String username){ //ok
        return usersByUsername.get(username.toLowerCase());
    }

    @Override
    public User retrieveUserByUserID(int userID){ //ok
        return usersById.get(userID);
    }


}
