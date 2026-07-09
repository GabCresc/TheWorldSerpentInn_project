package classes;

import logic.beans.BeanCampaign;
import logic.beans.BeanUser;
import logic.controllers.CampaignParticipationControl;
import logic.dao.CampaignDAO;
import logic.dao.ParticipationDAO;
import logic.exceptions.RequestAlreadySent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
 class RequestAlreadySentTest {


    @Mock
    private ParticipationDAO participationDAO;


    @Test
    void exceptionDoesOccur() throws RequestAlreadySent {
        CampaignParticipationControl participationControl = new CampaignParticipationControl(participationDAO);
        BeanCampaign beanCampaign = new BeanCampaign();
        beanCampaign.setCampId(101);

        beanCampaign.setMaxNumberOfPlayers(5);
        beanCampaign.setAcceptedPlayers(new ArrayList<>());

        BeanUser beanUser = new BeanUser();
        beanUser.setUserID(5);

        when(participationDAO.isRequestAlreadyPresent(beanUser.getUserID(), beanCampaign.getCampId()))
                .thenReturn(true);

        Exception exception = assertThrows(RequestAlreadySent.class, () -> participationControl.participate(beanCampaign, beanUser));

        String expectedMessage = "Richiesta di partecipazione già inviata a questa campagna!";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage),
                "Il messaggio dell'eccezione non è quello atteso.");
    }
}