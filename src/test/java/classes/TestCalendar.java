package classes;

import logic.beans.BeanCampaign;
import logic.controllers.LoginGoogleControl;
import logic.controllers.ManageRequestControl;
import logic.controllers.abstract_factory_dao.DaoFactory;
import logic.dao.CampaignDAO;
import logic.model.ModelCampaign;
import org.junit.jupiter.api.Test;

import java.util.logging.Level;
import java.util.logging.Logger;

public class TestCalendar {
    private static final Logger logger = Logger.getLogger(TestCalendar.class.getName());
     @Test
    void testSimulationReminder(){
        try {
            // 1. Avviamo l'autenticazione Google per prendere i permessi
            // Questo aprirà il browser web per farti fare il login con l'account The World Serpent Inn (o il tuo personale)
            LoginGoogleControl loginControl = new LoginGoogleControl();
            System.out.println("In attesa del login su Google...");
            loginControl.startAuthGoogle();
            System.out.println("Login Google completato! Avvio simulazione Calendar...");

            // 2. Prepariamo i dati finti (il codice che mi hai incollato)
            CampaignDAO dao = DaoFactory.getFactory().createCampaignDAO();
            // Assicurati che nel tuo database esista una campagna con ID = 1, altrimenti metti un ID valido!
            ModelCampaign model = dao.getCampaignById(1);

            if (model != null) {

                BeanCampaign bean = new BeanCampaign(model);
                ManageRequestControl manageRequestControl = new ManageRequestControl();

                manageRequestControl.simulationReminder(bean, "wserpentinn@gmail.com");
            } else {
                System.out.println("Errore: Nessuna campagna trovata con ID 1 nel database.");
            }

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Exception occurred while testing reminder");
        }
    }
}