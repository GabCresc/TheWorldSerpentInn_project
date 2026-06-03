package logic.controllers;

import logic.beans.BeanCampaign;
import logic.dao.UserDAO;
import logic.model.ModelCampaign;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import logic.dao.CampaignDAO;
import logic.beans.BeanUser;
import logic.model.User;
import logic.utils.enums.NotificationTypes;
import logic.utils.enums.UserTypes;
import logic.controllers.NotificationControl;

public class CreateCampaignControl {
    private static final Logger logger = Logger.getLogger(CreateCampaignControl.class.getName());

    public boolean createCampaign(BeanCampaign campaignBean){
        ModelCampaign campaign= new ModelCampaign(campaignBean);
        return CampaignDAO.addCampaign(campaignBean);
    }

    //metodo che vede se un giocatore è un player oppure no
    public boolean isPlayer(BeanUser user){
        UserTypes usertype = user.getUserType();
        return (usertype == UserTypes.PLAYER);
    }

    //metodo che prende il nome dell'user dalla sua stringa e aggiunge l'user alla lista dei giocatori da notificare se l'utente è un player
    public List<BeanUser> addNotifiedPlayer(List<BeanUser> invitingPlayers, String UserName){
            UserDAO userDAO = new UserDAO();
            BeanUser User = userDAO.retrievePlayer(UserName);
            if (isPlayer(User)){
                invitingPlayers.add(User);
            }
            return invitingPlayers;
    }

     // Notifica i giocatori scelti della creazione di una campagna da parte di un master
     public void notifyCreation(Integer campaignID, List<BeanUser> userList){
        NotificationControl notiControl= new NotificationControl();
        CampaignDAO campaignDAO = new CampaignDAO(); //probabilmente da cambiare
        ModelCampaign campaign = campaignDAO.getCampaignById(campaignID);
        int notifierID = campaign.getCampDmId();
        int notifiedID;
        for(int index=0; index<userList.size(); index+=1) {
            notifiedID = (userList.get(index)).getUserID();
            notiControl.sendServerNotification(NotificationTypes.CAMPAIGN_ADDED, notifierID, notifiedID, campaignID);
        }
        List
     }

     public void deleteCampaign(int campaignID){
         CampaignDAO.deleteCampaign(campaignID);
     }
}
