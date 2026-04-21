package logic.utils;
import logic.utils.enums.PersistenceTypes;

public class Persistence {
    private Persistence(){}

        // default impostato a JDBC. Utilizziamo private static per avere una sola copia
    private static PersistenceTypes per_type = PersistenceTypes.JDBC;
    public static void setPersistenceType(PersistenceTypes type){
        per_type = type;
    }
    public static PersistenceTypes getPersistenceType(){
        return per_type;
    }
}
