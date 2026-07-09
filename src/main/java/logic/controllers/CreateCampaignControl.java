package logic.controllers;

import logic.beans.BeanCampaign;
import logic.controllers.abstract_factory_dao.DaoFactory;
import logic.dao.UserDAO;
import logic.model.ModelCampaign;
import java.util.List;
import java.util.logging.Logger;
import logic.dao.CampaignDAO;
import logic.beans.BeanUser;
import logic.model.User;
import logic.utils.enums.NotificationTypes;
import logic.utils.enums.UserTypes;

public class CreateCampaignControl {
    private static final Logger logger = Logger.getLogger(CreateCampaignControl.class.getName());

    public boolean createCampaign(BeanCampaign campaignBean){
        ModelCampaign campaign = new ModelCampaign(campaignBean);
        DaoFactory factory = DaoFactory.getFactory();
        CampaignDAO dao = factory.createCampaignDAO();

        boolean isCreated = dao.addCampaign(campaign);

        if (isCreated) {
            campaignBean.setCampId(campaign.getCampId());
        }

        return isCreated;
    }

    public boolean isPlayer(BeanUser user){
        if (user == null || user.getUserType() == null) {
            return false;
        }
        return user.getUserType() == UserTypes.PLAYER;
    }

    public List<BeanUser> addNotifiedPlayer(List<BeanUser> invitingPlayers, String userName) {
        DaoFactory factory = DaoFactory.getFactory();
        UserDAO userDAO = factory.createUserDAO();

        User user = userDAO.retrieveUserByUsername(userName);

        if (user != null) {

            BeanUser beanUser = new BeanUser(user);

            if (isPlayer(beanUser)){
                invitingPlayers.add(beanUser);
            }
        }

        return invitingPlayers;
    }

    public void notifyCreation(Integer campaignID, List<BeanUser> userList) {
        NotificationControl notiControl = new NotificationControl();

        DaoFactory factory = DaoFactory.getFactory();
        CampaignDAO campaignDAO = factory.createCampaignDAO();

        ModelCampaign campaign = campaignDAO.getCampaignById(campaignID);
        if (campaign == null) {
            logger.warning("Impossibile inviare le notifiche: campagna non trovata.");
            return;
        }

        int notifierID = campaign.getCampDmId();

        for (BeanUser notifiedUser : userList) {
            if (notifiedUser != null) {
                int notifiedID = notifiedUser.getUserID();
                notiControl.sendServerNotification(
                        NotificationTypes.CAMPAIGN_ADDED,
                        notifierID,
                        notifiedID,
                        campaignID
                );
            }
        }
    }

    public void deleteCampaign(int campaignID){
        DaoFactory factory = DaoFactory.getFactory();
        CampaignDAO dao = factory.createCampaignDAO();
        dao.deleteCampaign(campaignID);
    }
}