package logic.DAO;

import com.opencsv.CSVWriter;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import logic.utils.enums.Notification_Kind;
import lombok.extern.java.Log;
import java.util.logging.Level; // suggerito da SonarCloud

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import logic.utils.enums.NotificationTypes;
import logic.model.Notification;
import java.util.ArrayList;
import logic.controllers.factory.NotificationFactory;

@Log
public class NotificationCSV implements NotificationDAO {

    private static final String CSV_name = "DBnotification.csv";
    private File fd;
    private Integer number_of_entries; // CAPIRE COSA FA LAST INDEX BENE E COME USARLO IN RELAZIONE A NOTIFICATIONID
    private NotificationFactory notifactory;

    private static class Order{
        private static final int FIRST = 0;
        private static final int SECOND = 1;
        private static final int THIRD = 2;
        private static final int FOURTH = 3;
        private static final int FIFTH = 4;
        private static final int SIXTH = 6;

        public static int getNotificationID(){
            return FIRST;
        }

        public static int getNotifierID(){
            return SECOND;
        }

        public static int getNotifiedID(){
            return THIRD;
        }

        public static int getType(){
            return FOURTH;
        }

        public static int getUserID(){
            return FIFTH;
        }

        public static int getCampaignID(){
            return SIXTH;
        }

    }

    public NotificationCSV(){
        // creazione cartella con dentro il file csv
        String folder_name = "csvDatabase";
        File folder = new File(folder_name);

        if(!folder.exists()){
            createFolder(folder);
        }else{
            log.log(Level.FINEST, "Folder already exist" + folder_name);
        }

        // Creazione filepath
        String filepath = folder_name + File.separator + CSV_name;

        // Inizializzazione fileDescriptor
        this.fd = new File(filepath);

        if(fd.exists()){

            try{
                Files.delete(Paths.get(filepath));
                log.log(Level.FINEST, "Existing CSV File deleted");
            } catch (IOException e) {
                log.log(Level.WARNING, "Error occurred while deleting CSV File");
            }
        }

        if(!this.fd.exists()){
            createFileUsingDescr(this.fd);
        }else{
            log.log(Level.FINEST, "CSV File already exists in path: " + folder_name + "/" + CSV_name);
        }
        // si aggiorna l'indice del file all'ultima entrata
        updateToLastIndex();
        // inizializzazione NotificationFactory
        this.notifactory = new NotificationFactory();
    }

    private void createFileUsingDescr(File fd){
        try {
            boolean result = fd.createNewFile();
            if (result) {
                log.log(Level.FINEST, "File created");
            } else {
                log.log(Level.WARNING, "Error occurred during CSV File creation");
            }
        }catch(IOException e){
            log.log(Level.SEVERE, "Error while creating new file: " + e.getMessage()); //ricontrollare bene cosa fa getmessage()
        }
    }

    private void createFolder(File folder){
        boolean res = folder.mkdirs();
        if (res) {
            log.log(Level.FINEST, "Folder created");
        }else {
            log.log(Level.FINEST, "Failed to create folder");
        }
    }

    // ID generato in base all'ultima posizione raggiunta + 1
    private void updateToLastIndex(){
        int count = 0;
        try(CSVReader csvReader = new CSVReader(new BufferedReader(new FileReader(fd)))){
            while(csvReader.readNext() != null) {
                count++;
            }
//CsvValidation: invalid regex matches or column count mismatches, which can be captured using withThrowExceptions(false) to
// inspect errors without stopping execution
        }catch(CsvValidationException | IOException e){
            log.log(Level.SEVERE, "Failed to update last index");
        }
        number_of_entries = count;
    }

    // il true serve a fare in modo che i dati vengano scritti alla fine del file, piuttosto che all'inizio, per evitare di riscrivere sul file
    @Override
    public void addNotification(NotificationTypes type, int notifierID, int notifiedID,
                         int notification_id, int campaign_id){
        try(CSVWriter csvWriter = new CSVWriter(new BufferedWriter(new FileWriter(fd, true)))){
            Notification_Kind kind = type.getKind();
            Notification msg;
            String[] csv_record = new String[6];

            //METTERE STO MECCANISMO DI SCELTA NELLA FACTORY
            /*if(kind == Notification_Kind.LOCAL){
                msg = notifactory.CreateLocalNotification(); // però dovrebbe prendere come param kind o type per sapere che il type
            }else{
                // notifica di tipo server
                msg = notifactory.CreateServerNotification();
            }*/

            // così ci pensa la factory a vedere se istanziare LOCAL o SERVER
            msg = notifactory.CreateNotification(notification_id, notifierID, notifiedID, type, campaign_id);

            csv_record[Order.getNotificationID()] = String.valueOf(++number_of_entries);
            csv_record[Order.getNotifierID()] = String.valueOf(notifierID);
            csv_record[Order.getNotifiedID()] = String.valueOf(notifiedID);
            csv_record[Order.getType()] = String.valueOf(type);
            csv_record[Order.getCampaignID()] = String.valueOf(campaign_id);


            csvWriter.writeNext(csv_record);
            csvWriter.flush();

        }catch(IOException e){
            log.log(Level.SEVERE, "IOException occurred while adding notification");
        }
    }

    @Override
    public ArrayList<Notification> getNotificationsByUserId(int user_id){
        ArrayList<Notification> list = new ArrayList<>();

        try(CSVReader csvReader = new CSVReader(new BufferedReader(new FileReader(fd)))){
            String[] record; //lista dei record letti
            Notification msg;
            while ((record = csvReader.readNext()) != null){
                // Assumendo che l'ordine delle colonne sia, in addNotification:
                // 0) notification_ID;
                // 1) notifier_name;
                // 2) notified_name;
                // 3) type;
                // 4) user_ID;
                // 5) campaign_ID
                if(Integer.parseInt(record[Order.getUserID()]) == user_id){
                    int notifId = Integer.parseInt(record[Order.getNotificationID()]);
                    int notifier = Integer.parseInt(record[Order.getNotifierID()]);
                    int notified = Integer.parseInt(record[Order.getNotifiedID()]);
                    NotificationTypes type = NotificationTypes.valueOf(record[Order.getType()]);
                    int campaignId = Integer.parseInt(record[Order.getCampaignID()]);

                    Notification_Kind kind = type.getKind();
                   /* //METTERE STO MECCANISMO DI SCELTA NELLA FACTORY
                    if(kind == Notification_Kind.LOCAL){
                        msg = notifactory.CreateLocalNotification(); // però dovrebbe prendere come param kind o type per sapere che il type
                    }else{
                        // notifica di tipo server
                        msg = notifactory.CreateServerNotification();
                    }*/
                    msg = notifactory.CreateNotification(notifId, notifier, notified, type, campaignId);
                    list.add(msg);
                }
            }

            }catch(CsvValidationException | IOException e){
                log.log(Level.SEVERE, "Exception occurred while getting notifications by userID (csv)");
        }
        return list;
    }

    @Override
    public boolean deleteNotification(int notification_id){
        try(CSVReader csvReader = new CSVReader(new BufferedReader(new FileReader(fd))); CSVWriter csvWriter = new CSVWriter(new BufferedWriter(new FileWriter(fd)))){
            String[] record;
            while((record = csvReader.readNext()) != null){
                if(Integer.parseInt(record[0]) != notification_id){
                    csvWriter.writeNext(record); //riscrivo tutte le notifiche tranne quella che voglio eliminare
                }
            }
        }catch(CsvValidationException | IOException e){
            log.log(Level.SEVERE, "Exception occurrec while deleting notification in CSV File");
            return false;
        }
        return moveTempCSV();
    }

    // metodo necessario perché se scrivessimo direttamente sul file originale, c'è il rischio che se il programma si
    // interrompe mentre avviene la scrittura, il file originale andrebbe perso.
    private boolean moveTempCSV(){
        try{
            Files.move(Paths.get("csvDatabase/temp.csv"), Paths.get(fd.toURI()), StandardCopyOption.REPLACE_EXISTING);
        }catch(IOException e){
            log.log(Level.SEVERE, "IOException occured while moving temporary files csv");
            return false;
        }

        return true;
    }
}
