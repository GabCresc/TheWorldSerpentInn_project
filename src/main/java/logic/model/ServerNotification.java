package logic.model;

import javafx.event.ActionEvent;
import logic.controllers.abstract_factory_dao.DaoFactory;
import logic.controllers.factory.NotificationFactory;
import logic.dao.CampaignDAO;
import logic.dao.NotificationDAO;
import logic.dao.UserDAO;
import logic.utils.enums.NotificationTypes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Properties;
import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.apache.commons.codec.binary.Base64;
import java.io.Serial;

public class ServerNotification implements Notification{
    private NotificationTypes notificationType;
    private int notifiedID;
    private int notifierID;
    private int notificationID;
    private int campaignID;
    private LocalDateTime startDate;
    private LocalTime timeSession;
    private String freq;
    private String city;
    private String userEmail;

    @Serial
    private static final long serialVersionUID = 1L;


    public ServerNotification(int notificationID, int notifierID, int notifiedID, NotificationTypes type, int campaignID){
        this.notificationID = notificationID;
        this.notifierID = notifierID;
        this.notifiedID = notifiedID;
        this.notificationType = type;
        this.campaignID = campaignID;
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

    public String getCity(){
        return city;
    }

    public String getUserEmail(){
        return userEmail;
    }

    public String getFrequency(){
        return freq;
    }

    public LocalTime getTimeSession(){
        return timeSession;
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

}
