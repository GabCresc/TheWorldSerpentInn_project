package logic.controllers;

import logic.beans.BeanCampaign;
import logic.beans.BeanFilter;
import logic.controllers.abstract_factory_dao.DaoFactory;
import logic.exceptions.RequestAlreadySent;
import logic.model.ModelCampaign;
import logic.dao.*;
import logic.utils.enums.NotificationTypes;
import logic.model.User;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.ArrayList;
import logic.beans.BeanUser;
import java.util.List;
import logic.utils.enums.Status;


public class CampaignParticipationControl{

    private static final Logger logger = Logger.getLogger(CampaignParticipationControl.class.getName());
    private ParticipationDAO participationDao = null;

    //costruttore di base
    public CampaignParticipationControl(){
        //empty
    }

    //costruttore per i test
    public CampaignParticipationControl(ParticipationDAO participationDAO){
        this.participationDao = participationDAO;
    }

    public boolean participate(BeanCampaign campBean, BeanUser userBean) throws RequestAlreadySent { // invia la richiesta di partecipazione -> dunque comunica con il
        // controller delle notifiche per inviare la richiesta al corrispettivo DM e inserisce il player nella lista waitingPlayers

        if (campBean.isFull()) {
            logger.log(Level.FINE, "Selected Campaign is full");
            return false;
        }
        // se participationDao è nulla, allora non stiamo usando quella di test e ne istanziamo una vera
        ParticipationDAO pDao =  (this.participationDao != null) ? this.participationDao : DaoFactory.getFactory().createParticipationDAO();
        if (pDao.isRequestAlreadyPresent(userBean.getUserID(), campBean.getCampId())) {
            logger.log(Level.FINE, "Already sent a request to this campaign");
            throw new RequestAlreadySent("Richiesta di partecipazione già inviata a questa campagna!");
        }

        pDao.addWaitingPlayer(userBean.getUserID(), campBean.getCampId());
        NotificationControl notificationControl = new NotificationControl();
        notificationControl.sendServerNotification(NotificationTypes.REQUEST_PARTICIPATION, userBean.getUserID(), campBean.getCampDMID(), campBean.getCampId());
        return true;
    }

    public List<BeanCampaign> getFilteredCampaigns(BeanFilter filter){
        List<BeanCampaign> beanCampaignList = new ArrayList<>();
        try{
            CampaignDAO campDao = DaoFactory.getFactory().createCampaignDAO();
            List<ModelCampaign> list = campDao.findCampaignByFilter(filter);
            for(ModelCampaign m:list){
                BeanCampaign bean = new BeanCampaign(m);
                beanCampaignList.add(bean);
            }

        }catch(Exception e){
            logger.log(Level.SEVERE, "Exception occurred in Filter", e);
        }

        return beanCampaignList;
    }

    public boolean removeParticipation(BeanCampaign campBean, BeanUser userBean) {
        boolean success = false;
        try {
            int campaignId = campBean.getCampId();
            int userid = userBean.getUserID();
            ParticipationDAO pDao = DaoFactory.getFactory().createParticipationDAO();
            success = pDao.removeRequestOfParticipation(userid, campaignId);

            if (success) {
                logger.log(Level.INFO, "Partecipation request successfully removed");
            } else {
                logger.log(Level.WARNING, "Failed to remove partecipation request");
            }

        }catch(Exception _){
            logger.log(Level.SEVERE, "Error occurred in controller while removing partecipation");
        }
        return success;
    }

    public List<BeanCampaign> getAvailableCampaigns(){

        CampaignDAO campDao = DaoFactory.getFactory().createCampaignDAO();
        List<ModelCampaign> listModels = campDao.retrieveCampaigns();

        List<BeanCampaign> beanList = new ArrayList<>();
        for (ModelCampaign m : listModels) {
            BeanCampaign bean = new BeanCampaign(m);
            beanList.add(bean);
        }
        return beanList;

    }

    public void showCampaignDetails(BeanCampaign bean){

        List<BeanUser> waitingBean = getBeanList(bean.getCampId(), Status.WAITING);
        List<BeanUser> acceptedBean = getBeanList(bean.getCampId(), Status.ACCEPTED);

        bean.setAcceptedPlayers(acceptedBean); //dati pronti per la view
        bean.setWaitingPlayers(waitingBean);
    }

    public List<BeanUser> getBeanList(int campID, Status status) {

        ParticipationDAO pDao = DaoFactory.getFactory().createParticipationDAO();
        List<BeanUser> beanUsers = new ArrayList<>();

        List<User> userModels = pDao.getPlayersByStatus(campID, status.toString());
        if (userModels == null) {
            return beanUsers;
        }
        for (User u : userModels) {
            BeanUser bean = new BeanUser(u);
            beanUsers.add(bean);

        }
        return beanUsers;
    }

    public String getDmNameById(int userID){
        UserDAO userDAO = DaoFactory.getFactory().createUserDAO();
        return userDAO.getUsernameByUserId(userID);
    }


}