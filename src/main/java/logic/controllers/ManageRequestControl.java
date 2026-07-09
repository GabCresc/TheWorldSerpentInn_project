package logic.controllers;

import com.google.api.services.calendar.Calendar;
import logic.beans.BeanCampaign;
import logic.beans.BeanUser;
import logic.controllers.abstract_factory_dao.DaoFactory;
import logic.dao.ParticipationDAO;
import logic.model.LocalNotification;
import logic.utils.enums.NotificationTypes;
import logic.utils.enums.Status;

import java.io.IOException;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ManageRequestControl {
    private static final Logger logger = Logger.getLogger(ManageRequestControl.class.getName());
    ParticipationDAO participationDao = DaoFactory.getFactory().createParticipationDAO();

    public boolean acceptPlayer(BeanCampaign beanCampaign, BeanUser beanUser) {
        if (beanCampaign.isFull()) {
            logger.log(Level.WARNING, "Campaign is full");
            return false;
        }
        try {
            boolean success = participationDao.acceptPlayer(beanUser.getUserID(), beanCampaign.getCampId(), Status.ACCEPTED);
            if (success) {

                BeanUser bean = null;
                for (BeanUser b : beanCampaign.getWaitingPlayers()) {
                    if (Objects.equals(b.getUserID(), beanUser.getUserID())) {
                        bean = b;
                        break;
                    }
                }

                if (bean != null) {
                    beanCampaign.getWaitingPlayers().remove(bean);
                    beanCampaign.getAcceptedPlayers().add(bean);
                    logger.log(Level.INFO, "Player moved to accepted players for the view.");
                }

                int dummyID = 0; // l'evento su Google o la notifica nel DB potrebbero non avere un'ID
                LocalNotification reminder = new LocalNotification(dummyID, beanCampaign.getCampDMID(), beanUser.getUserID(), NotificationTypes.REMINDER, beanCampaign.getCampId());
                LoginGoogleControl loginGoogleControl = new LoginGoogleControl();
                Calendar googleCalendar = loginGoogleControl.getCalendarService();
                reminder.setService(googleCalendar);
                reminder.setUserEmail(beanUser.getEmail());
                reminder.setupReminder();
                logger.log(Level.INFO, "Google Calendar Reminder sent!");
                return true;

            } else {
                logger.log(Level.INFO, "There was a problem while sending Google Calendar Reminder");
                return false;
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Exception occurred while setting up reminder or accepting player", e);
        }
        return false;
    }


    public boolean rejectPlayer(BeanCampaign beanCampaign, BeanUser beanUser) {
        try {
            ParticipationDAO pDao = DaoFactory.getFactory().createParticipationDAO();

            boolean success = pDao.acceptPlayer(beanUser.getUserID(), beanCampaign.getCampId(), Status.REJECTED);

            if (success) {
                beanCampaign.getWaitingPlayers().removeIf(b -> Objects.equals(b.getUserID(), beanUser.getUserID()));
                logger.log(Level.INFO, "Request rejected for player: {0}", beanUser.getUsername());
                return true;
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error in rejecting request", e);
        }
        return false;
    }
    public void simulationReminder(BeanCampaign campBean, String emailDiTest) {
        logger.log(Level.INFO, "Starting reminder simulation...");

        try {
            LocalNotification reminder = new LocalNotification(
                    999,
                    campBean.getCampDMID(),
                    777, // ID player finto
                    NotificationTypes.REMINDER,
                    campBean.getCampId()
            );
            LoginGoogleControl loginGoogleControl = new LoginGoogleControl();
            Calendar googleCalendar = loginGoogleControl.getCalendarService();
            reminder.setService(googleCalendar);
            reminder.setUserEmail(emailDiTest);
            reminder.setupReminder();

            logger.log(Level.INFO, "Google Calendar configured for {0}", emailDiTest);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Something went wrong with the simulation", e);
        }
    }
}
