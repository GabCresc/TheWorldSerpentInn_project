package logic.dao;

import com.opencsv.CSVWriter;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import logic.model.LocalNotification;
import logic.model.ModelCampaign;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import logic.utils.enums.NotificationTypes;
import logic.model.Notification;
import java.util.ArrayList;
import logic.controllers.factory.NotificationFactory;


public class NotificationCSV implements NotificationDAO {

    private static final Logger logger = java.util.logging.Logger.getLogger(NotificationCSV.class.getName());
    private static final String CSVNAME = "DbNotification.csv";
    private final File fd;
    private Integer numberOfEntries;
    private final NotificationFactory notifactory;

    private static class Order{
        private static final int FIRST = 0;
        private static final int SECOND = 1;
        private static final int THIRD = 2;
        private static final int FOURTH = 3;
        private static final int FIFTH = 4;

        public static int getNotificationID(){
            return FIRST;
        }

        public static int getNotifierID(){
            return SECOND;
        } //sarebbe lo userID

        public static int getNotifiedID(){
            return THIRD;
        }

        public static int getType(){
            return FOURTH;
        }

        public static int getCampaignID(){
            return FIFTH;
        }

    }

    public NotificationCSV(){

        // creazione cartella con dentro il file csv
        String folderName = "csvDatabase";
        File folder = new File(folderName);

        if(!folder.exists()){
            createFolder(folder);
        }else{
            logger.log(Level.FINEST, "Folder already exist {0}", folderName);
        }

        // Creazione filepath
        String filepath = folderName + File.separator + CSVNAME;

        // Inizializzazione fileDescriptor
        this.fd = new File(filepath);

        if(!this.fd.exists()){
            createFileUsingDescr(this.fd);
        }else{
            logger.log(Level.FINE, () -> "CSV File already exists in path: " + folderName + "/" + CSVNAME);
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
                logger.log(Level.INFO, "File created");
            } else {
                logger.log(Level.WARNING, "Error occurred during CSV File creation");
            }
        }catch(IOException e){
            logger.log(Level.SEVERE, "Error while creating new file: {0}", e.getMessage()); //ricontrollare bene cosa fa getmessage()
        }
    }

    private void createFolder(File folder){
        boolean res = folder.mkdirs();
        if (res) {
            logger.log(Level.INFO, "Folder created");
        }else {
            logger.log(Level.WARNING, "Failed to create folder");
        }
    }

    // ID generato in base all'ultima posizione raggiunta + 1
    private void updateToLastIndex(){
        int count = 0;
        try(CSVReader csvReader = new CSVReader(new BufferedReader(new FileReader(fd)))){
            while(csvReader.readNext() != null) {
                count++;
            }
//CsvValidation: accoppiamenti di regex invalidi regex o conteggio di colonne errato, che possono essere catturate usando withThrowExceptions(false) per
// analizzare gli errori senza interrompere l'esecuzione
        }catch(CsvValidationException | IOException e){
            logger.log(Level.SEVERE, "Failed to update last index", e);
        }
        numberOfEntries = count;
    }

    // il true serve a fare in modo che i dati vengano scritti alla fine del file, piuttosto che all'inizio, per evitare di riscrivere sul file
    @Override
    public int addNotification(Notification msg){ //teoricamente l'id è simulato da NumberOfEntries, ma msg deve per forza avere un'ID...

        if(msg instanceof LocalNotification){ //salviamo solo le notifiche server sul csv o sul database
            return msg.getNotificationID();
        }else if(msg == null){
            logger.log(Level.SEVERE, "Something went wrong with using parameters to create a notification");
        }

        try(CSVWriter csvWriter = new CSVWriter(new BufferedWriter(new FileWriter(fd, true)))){

            String[] csvRecord = new String[5];
            numberOfEntries++;
            int newId = numberOfEntries;

            msg.setNotificationID(newId);


            csvRecord[Order.getNotificationID()] = String.valueOf(newId);
            csvRecord[Order.getNotifierID()] = String.valueOf(msg.getNotifierID());
            csvRecord[Order.getNotifiedID()] = String.valueOf(msg.getNotifiedID()); //sarebbe userid
            csvRecord[Order.getType()] = String.valueOf(msg.getNotificationType());
            csvRecord[Order.getCampaignID()] = String.valueOf(msg.getCampaignID());


            csvWriter.writeNext(csvRecord);
            csvWriter.flush();
            return newId;

        }catch(IOException e){
            logger.log(Level.SEVERE, "IOException occurred while adding notification", e);
        }
        return -1;
    }

    @Override
    public ArrayList<Notification> getNotificationsByUserId(int userID){
        ArrayList<Notification> list = new ArrayList<>();
        CampaignJDBC daoCsv = new CampaignJDBC(); //FACTORY

        try(CSVReader csvReader = new CSVReader(new BufferedReader(new FileReader(fd)))){
            String[] recCsv; //lista dei record letti
            Notification msg;
            while ((recCsv = csvReader.readNext()) != null){
                if(Integer.parseInt(recCsv[Order.getNotifiedID()]) == userID){
                    int notifId = Integer.parseInt(recCsv[Order.getNotificationID()]);
                    int notifier = Integer.parseInt(recCsv[Order.getNotifierID()]);
                    NotificationTypes type = NotificationTypes.valueOf(recCsv[Order.getType()]);
                    int campaignId = Integer.parseInt(recCsv[Order.getCampaignID()]);

                    ModelCampaign camp = daoCsv.getCampaignById(userID);
                    msg = notifactory.createNotification(notifId, notifier, userID, type, campaignId);
                    //msg.setFreq(camp.getCampFreq());
                    msg.setTimeSession(camp.getCampTimeSession());
                    list.add(msg);
                }
            }

        }catch(CsvValidationException | IOException e){
            logger.log(Level.SEVERE, "Exception occurred while getting notifications by userID (csv)", e);
        }
        return list;
    }

    @Override
    public boolean deleteNotification(int notificationID){
        File tempFile = new File("csvDatabase/temp.csv");

        try(CSVReader csvReader = new CSVReader(new BufferedReader(new FileReader(fd))); CSVWriter csvWriter = new CSVWriter(new BufferedWriter(new FileWriter(tempFile)))){
            String[] recCsv;
            while((recCsv = csvReader.readNext()) != null){
                if(Integer.parseInt(recCsv[0]) != notificationID){
                    csvWriter.writeNext(recCsv); //riscrivo tutte le notifiche tranne quella che voglio eliminare
                }
            }
        }catch(CsvValidationException | IOException e){
            logger.log(Level.SEVERE, "Exception occurrec while deleting notification in CSV File", e);
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
            logger.log(Level.SEVERE, "IOException occured while moving temporary files csv", e);
            return false;
        }

        return true;
    }
}
