package classes;

import logic.beans.BeanCampaign;
import logic.beans.BeanFilter;
import logic.beans.BeanUser;
import logic.controllers.CampaignParticipationControl;
import logic.controllers.CreateCampaignControl;
import logic.controllers.NotificationControl;
import logic.exceptions.InvalidValueException;
import logic.exceptions.TextTooLongException;
import logic.model.Notification;
import logic.utils.SingletonLoggedUser;
import logic.utils.enums.Mode;
import logic.utils.enums.NotificationTypes;
import logic.utils.enums.UserTypes;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.testng.annotations.Test;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertEquals;

//Ivan Crescenzi
// create campaign and notification test

 class NotificationControlTest {
    public NotificationControlTest(){
        //empty
    }
    private SingletonLoggedUser loggedUser = SingletonLoggedUser.getInstance();
    private final Logger logger = Logger.getLogger(NotificationControlTest.class.getName());

    BeanCampaign campaign = new BeanCampaign();

    @BeforeEach
    public void setUp() {
        loggedUser.cleanSession();
    }

    @AfterEach
    public void clean() {
        loggedUser.cleanSession(); //puliamo la sessione
    }


    @Test
    void testCreateCampaign() {
        loggedUser.setUserType(UserTypes.DM);
        loggedUser.setUsername("MasterTest");
        loggedUser.setUserID(2);

        //Creazione campagna
        campaign.setDM(loggedUser.getUsername());
        campaign.setDmId(loggedUser.getUserID());
        NotificationControl notificationControl = new NotificationControl();
        CreateCampaignControl createCampaignControl = new CreateCampaignControl();
        CampaignParticipationControl campaignParticipationControl = new CampaignParticipationControl();


        campaign.setCampName("test");
        campaign.setCampMode(Mode.OFFLINE);
        campaign.setCampCity("Roma");
        campaign.setMaxNumberOfPlayers(4);
        campaign.setCampDate(LocalDateTime.of(2026, Month.AUGUST, 26, 14, 30));
        campaign.setTimeSession(LocalTime.of(21, 14, 30));
        campaign.setCampFreq("settimanale");
        createCampaignControl.createCampaign(campaign);




        //Divento player e controllo se esiste la campagna appena creata
        loggedUser.setUserType(UserTypes.PLAYER);
        loggedUser.setUserID(6);
        loggedUser.setUsername("UserTest");

        BeanFilter beanFilter = new BeanFilter();
        beanFilter.setMode(Mode.OFFLINE);
        beanFilter.setNameCampaign("test");
        List<BeanCampaign> campaigns=campaignParticipationControl.getFilteredCampaigns(beanFilter);


        assertEquals(campaign.getCampName(), campaigns.getFirst().getCampName());
        assertEquals(campaign.getCampDMID(), campaigns.getFirst().getCampDMID());

        //invio la notifica della creazione della campagna appena creata
        BeanUser user = new BeanUser();
        try {
            user.setUsername("UserTest");
        }catch(TextTooLongException | InvalidValueException e){
            logger.log(Level.SEVERE, e.getMessage());
        }
        List<BeanUser> userList = new ArrayList<>();
        user.setUserID(loggedUser.getUserID());
        user.setUserType(loggedUser.getUserType());
        userList.add(user);
        int campaignID = campaign.getCampId();

        createCampaignControl.notifyCreation(campaignID, userList);

        //Divento player e controllo se mi e' arrivata una notifica che la nuova campagna è stato creato
        loggedUser.setUserType(UserTypes.PLAYER);
        loggedUser.setUserID(6);
        loggedUser.setUsername("UserTest");

        List<Notification> myNotifications = notificationControl.retrieveNotifications(loggedUser.getUserID());
        assertEquals(NotificationTypes.CAMPAIGN_ADDED, myNotifications.getLast().getNotificationType());
    }
}