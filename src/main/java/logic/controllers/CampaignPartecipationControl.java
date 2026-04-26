package logic.controllers;

import logic.beans.Bean_Campaign;
import logic.beans.Bean_Filter;
import logic.beans.Bean_Notification;
import logic.model.Model_Campaign;
import logic.DAO.*;
import logic.DAO.PartecipationDAO;
import logic.utils.enums.Mode;
import lombok.extern.java.Log;
import logic.model.User;

import java.util.ArrayList;
import java.util.logging.Level; // suggerito da SonarCloud
import logic.beans.Bean_User;
import java.util.List;

@Log
public class CampaignPartecipationControl {
    private UserDAO userDao;

    public CampaignPartecipationControl(){
        userDao = new UserDAO();
    } // perché?

    public boolean partecipate(Bean_Campaign Cbean, Bean_User Ubean) { // invia la richiesta di partecipazione -> dunque comunica con il
        // controller delle notifiche per inviare la richiesta al corrispettivo DM e inserisce il player nella lista waitingPlayers
        Model_Campaign model = new Model_Campaign();
        if (Cbean.IsFull()) {
            // si potrebbe lanciare un'eccezione "CampaignFull". Nella gestione di questa eccezione si potrebbe
            // reinderizzare l'utente alla pagina iniziale (in qualche modo)
            log.log(Level.FINE, "Selected Campaign is full");
            return false;
        }
        PartecipationDAO pDAO = new PartecipationDAO();
        if (pDAO.isRequestAlreadyPresent(Ubean.getUserID(), Cbean.getCampId())) {
            log.log(Level.FINE, "Already sent a request to this campaign");
            return false;
        }
        pDAO.addWaitingPlayer(Ubean.getUserID(), Cbean.getCampId());
        //NotificationControl.SendNotification(dmID, playerID, campaignID;
        return true;
    }

    public void applyFilter(Bean_Filter filter){
        try{
            CampaignDAO cDAO = new CampaignDAO();
            List<Model_Campaign> list = cDAO.findCampaignByFilter(filter);
            //tramite un metodo della view va mostrato
        }catch(Exception e){ // anche qui si potrebbe mettere una exception personalizzata
            log.log(Level.SEVERE, "Exception occurred in Filter");
        }
    }

    //sarebbe cancelSelection
    // DA RICONTROLLARE
    public void removePartecipation(Bean_Campaign cbean, Bean_User cuser) {
        try {
            int campaignid = cbean.getCampId();
            int userid = cuser.getUserID(); // IMPORTANTE: quando avremo fatto il Singleton di LOGGED USER
            // inserire quel userid!!!
            PartecipationDAO pDAO = new PartecipationDAO();
            boolean success = pDAO.removeRequestOfPartecipation(userid, campaignid);
            if (success) {
                log.log(Level.INFO, "Partecipation request successfully removed");
            } else {
                log.log(Level.SEVERE, "Failed to remove partecipation request");
            }

        }catch(Exception e){
            log.log(Level.SEVERE, "Error occurred in controller while removing partecipation");
        }
    }

    public List<Bean_Campaign> getAvailableCampaigns(){
        CampaignDAO cDao = new CampaignDAO();
        List<Model_Campaign> list_models = cDao.retrieveCampaigns();
        //return list_models.stream().map(m -> new Bean_Campaign(m)).toList();
        // stream() converte una lista in uno stream
        // map crea una hashmap e poi si riconverte in lista

        List<Bean_Campaign> beanList = new ArrayList<>();
        for (Model_Campaign m : list_models) {
            Bean_Campaign bean = new Bean_Campaign(m);
            beanList.add(bean);
        }
        return beanList;
    }

    public void showCampaignDetails(Bean_Campaign bean){
        PartecipationDAO pDAO = new PartecipationDAO();

        List<User> accepted = pDAO.getPlayersByStatus(bean.getCampId(), "ACCEPTED");
        List<User> waiting = pDAO.getPlayersByStatus(bean.getCampId(), "WAITING");

        // necessito di metodi dalla bean user per settare i player che aspettano e i player accettati,
        // per convertire le model a delle bean (visto che poi è roba vista dalla view)
    }

    
}
