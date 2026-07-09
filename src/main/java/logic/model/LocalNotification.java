package logic.model;

import logic.controllers.abstract_factory_dao.DaoFactory;
import logic.dao.CampaignDAO;
import logic.utils.enums.NotificationTypes;
import java.time.LocalDateTime;
import java.time.LocalTime;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.EventAttendee;
import java.util.Arrays;
import java.util.ArrayList;
import java.io.IOException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import java.io.Serial;
import java.util.logging.Level;


public class LocalNotification implements Notification {
    private NotificationTypes notificationType;
    private int notifiedID;
    private int notifierID;
    private int notificationID;
    private int campaignID;
    private LocalDateTime startDate;
    private LocalTime timeSession;
    private transient Calendar service;
    private String city;
    private String userEmail;
    private String freq;
    private String frequenceString="";
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(LocalNotification.class.getName());
    private static final String TIMEZONE = "Europe/Rome";

    @Serial
    private static final long serialVersionUID = 1L;

    public LocalNotification(int notificationID, int notifierID, int notifiedID, NotificationTypes type, int campaignID){
        this.notificationID=notificationID;
        this.notifierID = notifierID;
        this.notifiedID = notifiedID;
        this.notificationType = type;
        this.campaignID = campaignID;
    }

    public void setupReminder() throws IOException{

        Event reminder = new Event();

        CampaignDAO campaignDAO = DaoFactory.getFactory().createCampaignDAO();
        ModelCampaign campaignModel=campaignDAO.getCampaignById(campaignID);
        String campaignName= campaignModel.getCampName();
        this.freq = campaignModel.getCampFreq();

        reminder.setSummary("Sessione di "+ campaignName);
        reminder.setLocation(city);

        ArrayList<EventAttendee> attendees = new ArrayList<EventAttendee>();

        attendees.add(new EventAttendee().setEmail(userEmail));
        reminder.setAttendees(attendees);

        if (this.startDate == null) {
            ModelCampaign camp = campaignDAO.getCampaignById(this.campaignID); // campaignId deve essere un attributo della classe
            if (camp != null) {
                this.startDate = camp.getCampStartDate();
            }
        }

        ZoneId timeZone = ZoneId.of(TIMEZONE);
        ZonedDateTime completeDate = startDate.atZone(timeZone);
        String startingDate = completeDate.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);

        DateTime start = DateTime.parseRfc3339(startingDate);
        DateTime end = DateTime.parseRfc3339(startingDate);
        reminder.setStart(new EventDateTime().setDateTime(start).setTimeZone(TIMEZONE));
        reminder.setEnd(new EventDateTime().setDateTime(end).setTimeZone(TIMEZONE));

        switch(freq){
            case "settimanale":
                frequenceString="RRULE:FREQ=WEEKLY";
                break;
            case "bisettimanale":
                frequenceString="RRULE:FREQ=WEEKLY;INTERVAL=2";
                break;
            case "mensile":
                frequenceString="RRULE:FREQ=MONTHLY";
                break;
            default:
                throw new IllegalArgumentException("Frequenza non supportata: " + freq);
        }
        reminder.setRecurrence(Arrays.asList(frequenceString));

        service.events().insert("primary", reminder).execute();
    }


    public void deleteReminder(String reminderID){
        try {
            service.events().delete("primary", reminderID).execute();
        }
        catch (IOException e){
            logger.log(Level.SEVERE, "Problema di rete con Google! Impossibile creare il promemoria.", e);
        }
    }

    //getter
    @Override
    public NotificationTypes getNotificationType(){
        return notificationType;
    }

    @Override
    public int getNotifierID(){
        return notifierID;
    }

    @Override
    public int getNotifiedID(){
        return notifiedID;
    }

    @Override
    public int getNotificationID(){
        return notificationID;
    }

    @Override
    public int getCampaignID(){
        return campaignID;
    }

    public LocalDateTime getStartDate(){
        return startDate;
    }

    public LocalTime getTimeSession(){
        return timeSession;
    }

    public String getCity(){
        return city;
    }

    public String getUserEmail(){
        return userEmail;
    }

    public String getFrequency(){
        return freq;
    }


    //setter
    @Override
    public void setNotificationType(NotificationTypes notificationType){
        this.notificationType = notificationType;
    }

    @Override
    public void setNotifierID(int notifierID){
        this.notifierID = notifierID;
    }

    @Override
    public void setNotifiedID(int notifiedID){
        this.notifiedID = notifiedID;
    }

    @Override
    public void setNotificationID(int notificationID){
        this.notificationID = notificationID;
    }

    @Override
    public void setCampaignID(int campaignID){
        this.campaignID = campaignID;
    }

    public void setStartDate(LocalDateTime startDate){
        this.startDate=startDate;
    }

    public void setTimeSession(LocalTime timeSession){
        this.timeSession=timeSession;
    }

    public void setCity(String city){
        this.city=city;
    }

    public void setUserEmail(String userEmail){
        this.userEmail=userEmail;
    }

    public void setFrequency(String freq){
        this.freq=freq;
    }

    public void setService(Calendar service){this.service = service;}


}