package logic.controllers;

import logic.beans.Bean_Campaign;
import logic.beans.Bean_Filter;
import logic.model.Model_Campaign;
import logic.DAO.*;
import logic.DAO.PartecipationDAO;
import logic.utils.enums.Mode;
import lombok.extern.java.Log;
import java.util.logging.Level; // suggerito da SonarCloud
import logic.beans.Bean_User;

@Log
public class CampaignPartecipationControl {
    private UserDAO userDao;

    public CampaignPartecipationControl(){
        userDao = new UserDAO();
    }

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
        //NotificationContro.SendNotification(dmID, playerID, campaignID;
        return true;
    }
    
}
