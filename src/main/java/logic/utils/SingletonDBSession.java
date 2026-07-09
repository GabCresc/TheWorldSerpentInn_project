package logic.utils;

import java.sql.Connection;
import java.io.IOException;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.io.*;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;


public class SingletonDBSession {

    private static final Logger logger = Logger.getLogger(SingletonDBSession.class.getName());

    private static SingletonDBSession instance = null;
    private String url;
    private String dbUser;
    private String dbPass;


    private SingletonDBSession(){
        //estraiamo le proprietà di configurazione dal file corrispondente
        try(InputStream input = Thread.currentThread().getContextClassLoader().getResourceAsStream("config.properties")){
            Properties prop = new Properties();
            if (input == null) {
                logger.log(Level.SEVERE, "Can't find config.properties");
                return;
            }
            prop.load(input);

            this.url = prop.getProperty("db.url");
            this.dbUser = prop.getProperty("db.user");
            this.dbPass = prop.getProperty("db.pass");
        }catch(IOException ex){
            logger.log(Level.SEVERE, "Exception occurred while loading db configuration", ex);
        }
    }

    public Connection startConnection() throws SQLException {
           return DriverManager.getConnection(url, dbUser, dbPass);
    }

    public static synchronized SingletonDBSession getInstance() {
        //singleton method
        if (instance == null) {
            instance = new SingletonDBSession();
        }
        return instance;
    }

    //non è necessario implementare un metodo closeConnection(), ci pensa il try-with-resources a chiudere la connessione

}

