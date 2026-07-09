package logic.utils;

import logic.utils.enums.PersistenceTypes;
import java.io.*;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Persistence {

    private static final Logger logger = Logger.getLogger(Persistence.class.getName());
    private static PersistenceTypes perType;

    private Persistence(){
        //empty
    }

    //applichiamo la lazy initialization
    public static synchronized PersistenceTypes getPersistence(){
        if(perType == null){
            logger.log(Level.INFO, "Loading file configuration");
            loadFromFile();
        }
        return perType;
    }

    public static void loadFromFile(){
        ClassLoader loader = Persistence.class.getClassLoader();
        try (InputStream input = loader.getResourceAsStream("config.properties")){
            if(input == null){
                perType = PersistenceTypes.JDBC;
                logger.log(Level.WARNING, "Can't find properties, setting JDBC by default");
                return;
            }
            Properties prop = new Properties();
            prop.load(input); //legge (apre canale di input byte) la lista di proprietà, creata come chiave - elemento, dove tutti gli elementi sono stringhe

            String persistence = prop.getProperty("persistence.type");
            if(persistence == null || persistence.isEmpty()){
                logger.log(Level.WARNING, "Persistence type not found, setting JDBC by default");
                perType = PersistenceTypes.JDBC; // JDBC di default
            }else{
                perType = PersistenceTypes.valueOf(prop.getProperty("persistence.type").toUpperCase());
            }

        }catch(IOException e){
            perType = PersistenceTypes.JDBC;
            logger.log(Level.WARNING, "Something went wrong with choosing persistence type. JDBC set by default", e);
        }
    }
}
