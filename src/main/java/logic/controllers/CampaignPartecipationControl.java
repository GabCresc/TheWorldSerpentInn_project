package logic.controllers;

import logic.beans.Bean_Campaign;
import logic.beans.Bean_Filter;
import logic.model.Model_Campaign;
import logic.DAO.*;
import logic.utils.enums.Mode;
import lombok.extern.java.Log;
import java.util.logging.Level; // suggerito da SonarCloud

@Log
public class CampaignPartecipationControl {
    private UserDAO userDao;

    public CampaignPartecipationControl(){
        userDao = new UserDAO();
    }

    public boolean partecipate(Bean_Campaign bean){ // invia la richiesta di partecipazione -> dunque comunica con il
        // controller delle notifiche per inviare la richiesta al corrispettivo DM e inserisce il player nella lista waitingPlayers
        Model_Campaign model = new Model_Campaign();
        if(bean.IsFull()){
            // si potrebbe lanciare un'eccezione "CampaignFull". Nella gestione di questa eccezione si potrebbe
            // reinderizzare l'utente alla pagina iniziale (in qualche modo)
            log.log(Level.FINE, "Selected Campaign is full");
        }


    }
}
