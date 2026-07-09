package logic.graphiccontrollers;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Line;
import logic.beans.BeanCampaign;
import logic.beans.BeanUser;
import logic.controllers.ManageRequestControl;
import logic.controllers.NotificationControl;
import logic.controllers.abstract_factory_dao.DaoFactory;
import logic.dao.CampaignDAO;
import logic.dao.UserDAO;
import logic.model.ModelCampaign;
import logic.model.Notification;
import logic.model.User;
import logic.utils.SingletonLoggedUser;
import logic.view.EssentialGUI;
import javafx.scene.input.MouseEvent;

import javafx.event.ActionEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import logic.utils.enums.NotificationTypes;
import logic.observer.Observer;


public class GCNotifications extends EssentialGUI implements Observer {

    @FXML AnchorPane mainPane;
    @FXML ImageView arrowUp;
    @FXML ImageView arrowDown;

    @FXML Label title1;
    @FXML Label title2;
    @FXML Label title3;
    @FXML Label title4;

    @FXML Label desc1;
    @FXML Label desc2;
    @FXML Label desc3;
    @FXML Label desc4;

    @FXML Button acceptBtn1;
    @FXML Button acceptBtn2;
    @FXML Button acceptBtn3;
    @FXML Button acceptBtn4;

    @FXML Button rejectBtn1;
    @FXML Button rejectBtn2;
    @FXML Button rejectBtn3;
    @FXML Button rejectBtn4;
    @FXML Label  notificationsNotFound;

    List<Label> titles;
    List<Label> descriptions;
    List<Button> acceptButtons;
    List<Button> rejectButtons;

    ArrayList<Notification> myNotifications;
    Integer notifPage = 0;

    Line separatingLine1;
    Line separatingLine2;
    Line separatingLine3;

    private NotificationControl notiController;

    private static final Logger logger = Logger.getLogger(GCNotifications.class.getName());
    static CampaignDAO campaignDAO = DaoFactory.getFactory().createCampaignDAO();
    static UserDAO userDAO = DaoFactory.getFactory().createUserDAO();

    @FXML
    private void initialize() {
        titles =  Arrays.asList(title1, title2, title3, title4);
        descriptions =  Arrays.asList(desc1, desc2, desc3, desc4);
        acceptButtons =  Arrays.asList(acceptBtn1, acceptBtn2, acceptBtn3, acceptBtn4);
        rejectButtons =  Arrays.asList(rejectBtn1, rejectBtn2, rejectBtn3, rejectBtn4);

        notiController = new NotificationControl();
        notiController.attach(this);

        int myId = SingletonLoggedUser.getInstance().getUserID();
        myNotifications = new ArrayList<>(notiController.retrieveNotifications(myId));

        notifPage = 0;
        showPage();
    }

    @Override
    public void update() {
        int myId = SingletonLoggedUser.getInstance().getUserID();
        myNotifications = new ArrayList<>(notiController.retrieveNotifications(myId));

        if (notifPage > 0 && (notifPage * 4) >= myNotifications.size()) {
            notifPage--;
        }

        showPage();
    }

    private void showPage() {
        ObservableList<Node> shapes = mainPane.getChildren();
        shapes.remove(separatingLine1);
        shapes.remove(separatingLine2);
        shapes.remove(separatingLine3);

        checkIfNotifEmpty();

        int remainingNotifs = myNotifications.size() - (notifPage * 4);

       buildNotif(remainingNotifs, shapes);

        for (int i = 0; i < 4; i++) {
            int indexNotifica = (notifPage * 4) + i;

            if (indexNotifica < myNotifications.size()) {
                Notification currentNotif = myNotifications.get(indexNotifica);

                ModelCampaign modelCampaign = campaignDAO.getCampaignById(currentNotif.getCampaignID());
                String campaignName = "";

                campaignName = checkIfModelNotifNull(modelCampaign);

                String title = "";
                String description = "";
                String notifierName = "";

                switch (currentNotif.getNotificationType()) {
                    case CAMPAIGN_ADDED:
                        title = "Campagna creata";
                        description = "La campagna " + campaignName + " è stata appena creata e sei stato invitato a parteciparvi.";
                        acceptButtons.get(i).setText("Segna come letto");
                        rejectButtons.get(i).setVisible(false);
                        break;

                    case REQUEST_PARTICIPATION:
                        title = "Richiesta di partecipazione";
                        //
                        User userReq = userDAO.retrieveUserByUserID(currentNotif.getNotifierID());
                        if (userReq != null) {
                            notifierName = new BeanUser(userReq).getUsername();
                        }
                        description = notifierName + " ha richiesto di partecipare alla tua campagna chiamata " + campaignName;
                        acceptButtons.get(i).setText("Accetta");
                        rejectButtons.get(i).setText("Rifiuta");
                        rejectButtons.get(i).setVisible(true);
                        break;

                    case ACCEPT_PARTICIPATION:
                        title = "Richiesta accettata";
                        User userAcc = userDAO.retrieveUserByUserID(currentNotif.getNotifierID());
                        if (userAcc != null) {
                            notifierName = new BeanUser(userAcc).getUsername();
                        }
                        description = notifierName + " ha accettato la tua richiesta di partecipazione alla campagna chiamata " + campaignName;
                        acceptButtons.get(i).setText("Segna come letto");
                        rejectButtons.get(i).setVisible(false);
                        break;
                    default:
                        logger.log(Level.SEVERE, "Notification type error");
                }

                titles.get(i).setText(title);
                titles.get(i).setVisible(true);
                descriptions.get(i).setText(description);
                descriptions.get(i).setVisible(true);
                acceptButtons.get(i).setVisible(true);

            } else {
                titles.get(i).setVisible(false);
                descriptions.get(i).setVisible(false);
                acceptButtons.get(i).setVisible(false);
                rejectButtons.get(i).setVisible(false);
            }
        }
    }

    public void checkIfNotifEmpty(){
        if (myNotifications.isEmpty()) {
            notificationsNotFound.setText("Attualmente non ci sono notifiche!");
            notificationsNotFound.setVisible(true);
        } else {
            notificationsNotFound.setText("");
            notificationsNotFound.setVisible(false);
        }
    }

    public void buildNotif(int remainingNotifs, ObservableList<Node> shapes){
        if (remainingNotifs >= 4) {
            separatingLine3 = new Line(193, 590, 1136, 590);
            shapes.add(separatingLine3);
        }
        if (remainingNotifs >= 3) {
            separatingLine2 = new Line(193, 440, 1136, 440);
            shapes.add(separatingLine2);
        }
        if (remainingNotifs >= 2) {
            separatingLine1 = new Line(193, 290, 1136, 290);
            shapes.add(separatingLine1);
        }
    }

    public String checkIfModelNotifNull(ModelCampaign modelCampaign){
        if (modelCampaign != null) {
            BeanCampaign beanCampaign = new BeanCampaign(modelCampaign);
            return beanCampaign.getCampName();
        }
        logger.log(Level.WARNING, "ModelCampaign is null. Can't retrieve username");
        return "";
    }

    @FXML
    private void prevNotifs(MouseEvent event) {
        if(notifPage > 0) {
            notifPage -= 1;
            showPage();
        }
    }

    @FXML
    private void nextNotifs(MouseEvent event) {
        if (myNotifications.isEmpty()) return;

        int maxPage = (myNotifications.size() - 1) / 4;

        if (notifPage < maxPage) {
            notifPage += 1;
            showPage();
        }
    }

    @FXML
    private void removeNotification(int indexNotification, boolean accepting){
        Notification currentNotif = myNotifications.get(indexNotification);
        if((currentNotif.getNotificationType() == NotificationTypes.REQUEST_PARTICIPATION)){
            //recuperiamo la campagna
            ModelCampaign modelCampaign = campaignDAO.getCampaignById(currentNotif.getCampaignID());
            BeanCampaign beanCampaign = new BeanCampaign(modelCampaign);
            //recuperiamo l'utente
            User user = userDAO.retrieveUserByUserID(currentNotif.getNotifierID());
            BeanUser beanUser = new BeanUser(user);

            ManageRequestControl manageRequestControl = new ManageRequestControl();
            if(accepting){
                boolean accepted = manageRequestControl.acceptPlayer(beanCampaign, beanUser);
                if(!accepted){
                    logger.log(Level.WARNING, "Can't accept player. Check if campaign is full");
                    return; //non cancelliamo la notifica
                }
            }else{
                boolean rejected = manageRequestControl.rejectPlayer(beanCampaign, beanUser);
                if(!rejected){
                    logger.log(Level.WARNING, "Can't reject player");
                    return; //non cancelliamo la notifica
                }
            }
        }

        notiController.deleteNotification(currentNotif.getNotificationID());
    }

    @FXML
    private void readOrAccept1(ActionEvent event) {
        int acceptedNotification = notifPage * 4;
        removeNotification(acceptedNotification, true);
    }

    @FXML
    private void readOrAccept2(ActionEvent event) {
        int acceptedNotification = (notifPage * 4) + 1;
        removeNotification(acceptedNotification, true);
    }

    @FXML
    private void readOrAccept3(ActionEvent event) {
        int acceptedNotification = (notifPage * 4) + 2;
        removeNotification(acceptedNotification, true);
    }

    @FXML
    private void readOrAccept4(ActionEvent event) {
        int acceptedNotification = (notifPage * 4) + 3;
        removeNotification(acceptedNotification, true);
    }

    @FXML
    private void reject1(ActionEvent event) {
        int rejectedNotification = notifPage * 4;
        removeNotification(rejectedNotification, false);
    }

    @FXML
    private void reject2(ActionEvent event) {
        int rejectedNotification = (notifPage * 4) + 1;
        removeNotification(rejectedNotification, false);
    }

    @FXML
    private void reject3(ActionEvent event) {
        int rejectedNotification = (notifPage * 4) + 2;
        removeNotification(rejectedNotification, false);
    }

    @FXML
    private void reject4(ActionEvent event) {
        int rejectedNotification = (notifPage * 4) + 3;
        removeNotification(rejectedNotification, false);
    }
}