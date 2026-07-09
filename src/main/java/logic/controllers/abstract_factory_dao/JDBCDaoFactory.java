package logic.controllers.abstract_factory_dao;

import logic.dao.*;

public class JDBCDaoFactory extends DaoFactory{
    @Override
    public UserDAO createUserDAO(){return new UserJDBC();}
    @Override
    public CampaignDAO createCampaignDAO(){return new CampaignJDBC();}
    @Override
    public ParticipationDAO createParticipationDAO(){return new ParticipationJDBC();}
    @Override
    public NotificationDAO createNotificationDAO(){return new NotificationJDBC();}
}
