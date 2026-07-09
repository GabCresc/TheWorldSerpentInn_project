package classes;

import logic.beans.BeanCampaign;
import logic.beans.BeanUser;
import logic.controllers.CampaignParticipationControl;
import logic.controllers.abstract_factory_dao.DaoFactory;
import logic.dao.UserDAO;
import logic.model.User;
import logic.utils.SingletonLoggedUser;
import logic.utils.enums.UserTypes;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

//Test della classe CampaignParticipationControl
//Arianna Gabrieli

 class CampaignParticipationControlTest {

    private final SingletonLoggedUser loggedUser = SingletonLoggedUser.getInstance();
    private final CampaignParticipationControl participationControl = new CampaignParticipationControl();
    private final UserDAO userDAO = DaoFactory.getFactory().createUserDAO();
    List<BeanCampaign> campaignList;

    @BeforeEach
     void setUp() {

        loggedUser.cleanSession();
        this.campaignList = participationControl.getAvailableCampaigns(); //prendiamo la lista di campagne aggiornata

    }

    @AfterEach
     void clean() {
        loggedUser.cleanSession(); //puliamo la sessione
    }

    @Test
     void participateToCampaign() {

        //utente fittizio
        String fakeID = String.valueOf(System.currentTimeMillis());
        String fakeUsername = "TEST_PLAYER_ADD" + fakeID;
        String fakeEmail = fakeID + "@example.com";

        User fakeUser = new User();
        fakeUser.setUsername(fakeUsername);
        fakeUser.setEmail(fakeEmail);
        fakeUser.setUserType(UserTypes.PLAYER);

        //recuperiamo l'utente fittizio dal database per prendere il suo ID
        userDAO.registerUser(fakeUser);
        User retrievedUser = userDAO.retrieveUserByUsername(fakeUser.getUsername());

        loggedUser.setUserID(retrievedUser.getUserID());
        loggedUser.setUserType(retrievedUser.getUserType());
        loggedUser.setEmail(retrievedUser.getEmail());
        loggedUser.setUsername(retrievedUser.getUsername());

        //controlliamo che esista almeno una campagna e prendiamo la prima
        this.campaignList = participationControl.getAvailableCampaigns();
        assertFalse(campaignList.isEmpty(), "At least one campaign must exist in database");
        BeanCampaign chosenCampaign = this.campaignList.getFirst();

        BeanUser chosenUser = new BeanUser(retrievedUser);

        boolean result = participationControl.participate(chosenCampaign, chosenUser);
        assertTrue(result, "Participation result should be true");

    }


    @Test
     void removeParticipation() {

        String uniqueId = String.valueOf(System.currentTimeMillis());
        String fakeUsername = "TEST_PLAYER_REMOVE" + uniqueId;
        String fakeEmail = uniqueId + "@example.com";

        //utente fittizio
        User fakeUser = new User();
        fakeUser.setUsername(fakeUsername);
        fakeUser.setEmail(fakeEmail);
        fakeUser.setUserType(UserTypes.PLAYER);

        userDAO.registerUser(fakeUser);

        // recuperiamo l'utente per avere l'ID nel database
        User retrievedUser = userDAO.retrieveUserByUsername(fakeUsername);

        loggedUser.setUserID(retrievedUser.getUserID());
        loggedUser.setUserType(UserTypes.PLAYER);
        BeanUser beanUser = new BeanUser(retrievedUser);


        BeanCampaign chosenCampaign = this.campaignList.getFirst();

        // creiamo la partecipazione, per poi rimuoverla
        boolean setupSuccess = participationControl.participate(chosenCampaign, beanUser);
        assertTrue(setupSuccess, "Inserted request of participation should be successful");

        // rimuoviamola
        boolean result = participationControl.removeParticipation(chosenCampaign, beanUser);


        assertTrue(result, "Remotion of request should be successful");
    }
}