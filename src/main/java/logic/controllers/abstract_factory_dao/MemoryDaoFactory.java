package logic.controllers.AbstractFactoryDao;

import logic.dao.*;

public class MemoryDaoFactory extends DaoFactory{
    @Override
    public UserDAO createUserDAO(){return new UserMemory();}
    @Override
    public CampaignDAO createCampaignDAO(){return new CampaignMemory();}
    @Override
    public ParticipationDAO createParticipationDAO(){return new ParticipationMemory();}
    @Override
    public NotificationDAO createNotificationDAO(){return new NotificationMemory();}
}
