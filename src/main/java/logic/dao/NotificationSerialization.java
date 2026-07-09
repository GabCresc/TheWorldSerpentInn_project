package logic.dao;
//far implementare l'interfaccia Serializable alle Notification

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;
import java.util.logging.Level;
import logic.controllers.factory.NotificationFactory;
import logic.model.LocalNotification;
import logic.model.Notification;

public class NotificationSerialization implements NotificationDAO{

    private static final Logger logger = Logger.getLogger(NotificationSerialization.class.getName());
    private static final String FILENAME = "DbNotification.dat"; //per i binari
    private File fd;

    private int lastId = 0;

    public NotificationSerialization(){ //ok
        String foldername = "DbSerial";
        File folder = new File(foldername);

        if(!folder.exists()){
            boolean success = folder.mkdirs();
            if(success){
                logger.log(Level.INFO, "Folder created: {0}", foldername);
            }else{
                logger.log(Level.WARNING, "Failed to create folder");
            }
        }

        String filepath = foldername + File.separator + FILENAME;
        this.fd = new File(filepath);

        init();
    }

    public void init(){ //ok
        if(!this.fd.exists()) {
            try {
                if (this.fd.createNewFile()) {
                    save(new ArrayList<>());
                    logger.log(Level.INFO, "Binary file created");
                }
            }catch(IOException e){
                logger.log(Level.SEVERE, "IOException occurred while creating binary file");
            }
        }else{
            List<Notification> listOfNotif = load();
            for(Notification n : listOfNotif){
                if(n.getNotificationID() > lastId){
                    lastId = n.getNotificationID();
                }
            }
        }
    }

    private List<Notification> load(){ //ok
       if(!fd.exists() || fd.length() == 0) {
           return new ArrayList<>();
       }
       try(ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(fd))){
            return (List<Notification>) inputStream.readObject();

       }catch(IOException | ClassNotFoundException e){
            logger.log(Level.SEVERE, "Exception occurred while loading from binary file", e);
            return new ArrayList<>();
           }
    }

    private boolean save(List<Notification> list){ //ok
        try(ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(fd))){
            outputStream.writeObject(list);
            return true;
        }catch(IOException e){
            logger.log(Level.SEVERE, "Exception occurred while saving on binary file", e);
            return false;
        }
    }

    @Override
    public int addNotification(Notification msg){ //ok
        if(msg instanceof LocalNotification){
            return msg.getNotificationID();
        }else if(msg == null){
            logger.log(Level.SEVERE, "Cannot add null notification");
            return -1;
        }

        List <Notification> currentNotif = load();
        this.lastId++;
        int newId = this.lastId;
        msg.setNotificationID(lastId);
        currentNotif.add(msg);
        if(save(currentNotif)){
            return newId;
        }
        return -1;
    }

    @Override
    public ArrayList<Notification> getNotificationsByUserId(int userId){ //ok
        List<Notification> listOfNotif = load();
        ArrayList<Notification> user = new ArrayList<>();
        for(Notification n : listOfNotif){
            if(n.getNotifiedID() == userId){
                user.add(n);
            }
        }
        return user;
    }

    @Override
    public boolean deleteNotification(int notificationId) {
        List<Notification> listOfNotif = load();
        Iterator<Notification> iterator = listOfNotif.iterator(); //usare i metodi della lista porta a CurrentModificationException
        boolean removed = false;
        while(iterator.hasNext()) {
            Notification n = iterator.next();
            if (n.getNotificationID() == notificationId) {
                iterator.remove();
                removed = true;
                break;
            }
        }if(removed){
            return save(listOfNotif);
        }
        return false;
    }
}
