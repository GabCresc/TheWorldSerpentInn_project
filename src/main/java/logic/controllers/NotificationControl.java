package logic.controllers;


import logic.controllers.abstract_factory_dao.DaoFactory;
import logic.controllers.factory.NotificationFactory;
import logic.dao.NotificationDAO;
import logic.dao.UserDAO;
import logic.model.Notification;
import logic.utils.enums.NotificationTypes;
import logic.observer.Observer;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.logging.Level;

public class NotificationControl {
    private static final Logger logger = Logger.getLogger(NotificationControl.class.getName());

    private NotificationDAO notificationDAO;
    private NotificationFactory notiFactory;

    // Observer
    private List<Observer> observers = new ArrayList<>();

    // Caching
    private List<Notification> cachedNotifications;

    public NotificationControl() {
        DaoFactory factory = DaoFactory.getFactory();
        this.notificationDAO = factory.createNotificationDAO();
        this.cachedNotifications = new ArrayList<>();
        this.notiFactory = new NotificationFactory();
    }


    public void attach(Observer observer) {
        if (!observers.contains(observer)) {
            observers.add(observer);
        }
    }


    public void notifyObservers() {
        for (Observer observer : observers) {
            observer.update();
        }
    }

    //Recupera la lista delle notifiche di un utente
    public List<Notification> retrieveNotifications(int userID) {
        // Se la cache è vuota, faccio la query al Database (CSV o JDBC)
        if (this.cachedNotifications.isEmpty()) {
            this.cachedNotifications = this.notificationDAO.getNotificationsByUserId(userID);
            logger.log(Level.INFO, "Notifiche caricate dal Database per l''utente {0}", userID);
        }
        // Restituisco la lista in memoria
        return this.cachedNotifications;
    }

    public void invalidateCache() {
        this.cachedNotifications.clear();
        logger.info("Cache delle notifiche svuotata. Al prossimo controllo verranno ricaricate dal DB.");
    }


    //cancello una notifica dal database
    public boolean deleteNotification(int notificationID) {
        // 1. Cancello dal Database
        if (this.notificationDAO.deleteNotification(notificationID)) {
            // 2. Aggiorno la cache in memoria
            cachedNotifications.removeIf(n -> n.getNotificationID() == notificationID);

            // 3. Avviso la GUI che la lista è cambiata (così si ridisegna)
            notifyObservers();
            return true;
        }
        return false;
    }


    /*
     * "Inviare" una notifica a un altro utente significa salvarla nel Database.
     * L'altro utente la vedrà quando farà il login o aprirà la sua pagina notifiche.
     */

    public void sendServerNotification(NotificationTypes type, int notifierID, int notifiedID, int campaignID) {
        try {
            Notification noti = this.notiFactory.createServerNotification(-1, notifierID, notifiedID, type, campaignID);

            //Salvo nel Database
            this.notificationDAO.addNotification(noti);

            logger.log(Level.INFO, "Notifica salvata nel DB. Destinatario: {0}", notifiedID);

            //Svuoto la cache
            invalidateCache();

            //Avviso gli Observer
            this.notifyObservers();

        } catch (Exception e) {
            logger.severe("Errore nel salvataggio della notifica: " + e.getMessage());
        }
    }

    /**
     * Creare una notifica locale per se stessi (Reminder).
     * Questa viene salvata nel DB e aggiunta subito alla cache per essere mostrata a schermo.
     */

    public void createLocalNotification(NotificationTypes type, int currentUserID, int campaignID) {
        try {
            Notification localNoti = notiFactory.createLocalNotification(-1, -1, currentUserID, type, campaignID);

            // 1. Salvo nel DB (e recupero l'ID generato per tenerlo aggiornato) da sistemare
            int generatedID = this.notificationDAO.addNotification(localNoti);
            localNoti.setNotificationID(generatedID);

            // 2. Aggiungo alla CACHE locale
            this.cachedNotifications.add(localNoti);

            // 3. Avviso la GUI (che farà il refresh mostrando il nuovo Reminder)
            notifyObservers();

            logger.info("Notifica locale creata e aggiunta alla vista.");

        } catch (Exception e) {
            logger.severe("Errore nella creazione della notifica locale: " + e.getMessage());
        }
    }

    public void clearCache() {
        this.cachedNotifications.clear();
        logger.info("Cache notifiche svuotata (es. a seguito di un logout).");
    }
}