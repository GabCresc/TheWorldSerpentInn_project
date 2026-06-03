package logic.controllers;

import logic.beans.BeanCampaign;
import logic.beans.BeanFilter;
import logic.model.ModelCampaign;
import logic.dao.*;
import logic.dao.ParticipationJDBC;
import logic.observer.Subject;
import logic.utils.enums.NotificationTypes;
import logic.model.User;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.ArrayList;
import logic.beans.BeanUser;
import java.util.List;
import logic.beans.BeanNotificationData;
import logic.utils.enums.Status;


public class CampaignPartecipationControl extends Subject{

    private static final Logger logger = Logger.getLogger(CampaignPartecipationControl.class.getName());

    private BeanNotificationData lastPartecipation;  //subjectState che descrive l'evento

    public boolean partecipate(BeanCampaign campBean, BeanUser userBean) { // invia la richiesta di partecipazione -> dunque comunica con il
        // controller delle notifiche per inviare la richiesta al corrispettivo DM e inserisce il player nella lista waitingPlayers

        if (campBean.isFull()) {
            // si potrebbe lanciare un'eccezione "CampaignFull". Nella gestione di questa eccezione si potrebbe
            // reinderizzare l'utente alla pagina iniziale (in qualche modo)
            logger.log(Level.FINE, "Selected Campaign is full");
            return false;
        }

        ParticipationJDBC pDao = new ParticipationJDBC();
        if (pDao.isRequestAlreadyPresent(userBean.getUserID(), campBean.getCampId())) {
            logger.log(Level.FINE, "Already sent a request to this campaign");
            return false;
        }

        pDao.addWaitingPlayer(userBean.getUserID(), campBean.getCampId());
        this.lastPartecipation = new BeanNotificationData("New partecipation request from: ", NotificationTypes.REQUEST_PARTECIPATION,
                userBean.getUserID(), campBean.getCampId());

        this.lastPartecipation.setUserID(campBean.getCampId()); //lo user finale è il dm, in quanto abbiamo scelto di
        // far apparire la notifica nella sezione notifiiche del dm

        notifyObs(); // notifichiamo l'osservatore (NotificationControl)
        return true;
    }

    public void applyFilter(BeanFilter filter){
        try{
            CampaignJDBC campDao = new CampaignJDBC();
            List<ModelCampaign> list = campDao.findCampaignByFilter(filter);
            //tramite un metodo della view va mostrato

        }catch(Exception _){ // anche qui si potrebbe mettere una exception personalizzata
            logger.log(Level.SEVERE, "Exception occurred in Filter");
        }
    }

    //sarebbe cancelSelection

    public void removePartecipation(BeanCampaign campBean, BeanUser userBean) {

        try {
            int campaignid = campBean.getCampId();
            int userid = userBean.getUserID(); // IMPORTANTE: quando avremo fatto il Singleton di LOGGED USER
            // inserire quel userid!!!
            ParticipationJDBC pDao = new ParticipationJDBC();
            boolean success = pDao.removeRequestOfParticipation(userid, campaignid);

            if (success) {
                logger.log(Level.INFO, "Partecipation request successfully removed");
            } else {
                logger.log(Level.WARNING, "Failed to remove partecipation request");
            }

        }catch(Exception _){
            logger.log(Level.SEVERE, "Error occurred in controller while removing partecipation");
        }
    }

    public List<BeanCampaign> getAvailableCampaigns(){

        CampaignJDBC campDao = new CampaignJDBC();
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

        ParticipationJDBC pDao = new ParticipationJDBC();
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

    public BeanNotificationData getLastPartecipation(){
        return this.lastPartecipation;
    }

}
