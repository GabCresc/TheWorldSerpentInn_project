package logic.controllers.abstract_factory_dao;

import logic.dao.*;
import logic.utils.Persistence;
import logic.utils.enums.PersistenceTypes;

public abstract class DaoFactory {

    public abstract UserDAO createUserDAO();
    public abstract CampaignDAO createCampaignDAO();
    public abstract ParticipationDAO createParticipationDAO();
    public abstract NotificationDAO createNotificationDAO();

    public static DaoFactory getFactory() {
        PersistenceTypes type = Persistence.getPersistence();
        switch (type) {
            case JDBC:
                return new JDBCDaoFactory();
            case SERIALIZATION:
                return new SerializationDaoFactory();
            case MEMORY:
                return new MemoryDaoFactory();
        }
        return new JDBCDaoFactory();
    }
}





