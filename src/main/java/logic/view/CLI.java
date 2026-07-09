package logic.view;

import logic.beans.BeanCampaign;
import logic.beans.BeanUser;
import logic.controllers.*;
import logic.controllers.abstract_factory_dao.DaoFactory;
import logic.dao.CampaignDAO;
import logic.dao.NotificationDAO;
import logic.dao.UserDAO;
import logic.exceptions.InvalidValueException;
import logic.exceptions.TextTooLongException;
import logic.exceptions.UsernameTaken;
import logic.model.ModelCampaign;
import logic.model.Notification;
import logic.model.User;
import logic.utils.SingletonLoggedUser;
import logic.utils.enums.Mode;
import logic.utils.enums.NotificationTypes;
import logic.utils.enums.UserTypes;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CLI {

    private static final String HELP = "/help";
    private static final String ONLINE = "online";
    private static final String OFFLINE = "offline";
    private static final String PASSWORD = "Inserisci password: ";
    private static final String[] COMMANDS_LIST = {HELP, "/home", "/viewcampaigns", "/createcampaign", "/login", "/logout", "/viewnotifications", "/registration", "/exit"};
    private static boolean execution = true;

    private static final Logger logger = Logger.getLogger(CLI.class.getName());
    public static final Scanner scanner = new Scanner(System.in);
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(CLI.class);

    static CreateCampaignControl createCampaignControl = new CreateCampaignControl();
    static LoginControl loginControl = new LoginControl();
    static NotificationControl notificationControl = new NotificationControl();
    static LoginGoogleControl loginGoogleControl = new LoginGoogleControl();


    static SingletonLoggedUser currentUser;

    public static String readInput() {
        while (true) {
            String input = scanner.nextLine().trim();
            if (input.startsWith("/")) {
                helpCase(input);
                boolean validCommand = false;
                for (String cmd : COMMANDS_LIST) {
                    if (cmd.equalsIgnoreCase(input)) {
                        validCommand = true;
                        break;
                    }
                }
                if (validCommand) {
                    throw new CommandSignalException(input);
                } else {
                    System.out.print("Comando non riconosciuto. Scrivi /help per la lista.\nRipeti l'inserimento: ");
                    continue;
                }
            }
            return input;
        }

    }

    public static void helpCase(String input) {
        if (input.equalsIgnoreCase(HELP)) {
            spacer(1);
            System.out.print("Lista dei comandi: ");
            for (String s : COMMANDS_LIST) {
                System.out.print(s + " ");
            }
            spacer(1);
            System.out.print("\nContinua l'inserimento: ");
        }


    }


    public static void spacer(int times) {
        for (int i = 0; i < times; i++) {
            System.out.print("\n");
        }
    }

    @SuppressWarnings("unused")
    public static void main(String[] args) {
        loadHomePage();
        String waitingCommand = null;
        while (execution) {
            try {
                String input;
                if (waitingCommand != null) {
                    input = waitingCommand;
                    waitingCommand = null;
                } else {
                    System.out.print("Inserisci comando: ");
                    input = readInput();
                }
                 handleCommand(input);
            } catch (CommandSignalException e) {
                waitingCommand = e.getCommand();
            } catch (InvalidValueException | UsernameTaken | TextTooLongException e) {
                System.out.println("Errore durante l'operazione: " + e.getMessage());
            }
        }
    }

    private static void loadHomePage() {
        System.out.print("""
                Benvenuto su\s
                ##    ###    ##   #######  #######   ##      #######        ####### #######  #######    #######  #######   ##      ##   ##########     ##   ##      ##   ##      ##
                ##    ###    ##  ##     ## ##     ## ##      ##     ##     ##       ##       ##    ##   ##    ## ##       ## ##    ##       ##         ##  ## ##    ##  ## ##    ##
                ##   ## ##   ##  ##     ## ##     ## ##      ##     ##     ##       ##       ##    ##   ##    ## ##       ##  ##   ##       ##         ##  ##  ##   ##  ##  ##   ##
                ##   ## ##   ##  ##     ## ## ###    ##      ##     ##     #######  #######  #######    #######  #######  ##   ##  ##       ##         ##  ##   ##  ##  ##   ##  ##
                 ## ##   ## ##   ##     ## ##    ##  ##      ##     ##          ##  ##       ##   ##    ##       ##       ##    ## ##       ##         ##  ##    ## ##  ##    ## ##
                 ## ##   ## ##   ##     ## ##     ## ##      ##     ##          ##  ##       ##    ##   ##       ##       ##     ####       ##         ##  ##     ####  ##     ####
                  ###     ###     #######  ##      # ####### #######       ######   #######  ##     ##  ##       #######  ##      ##        ##         ##  ##      ##   ##      ##
                """);
    }

    private static void handleCommand (String command) throws
            InvalidValueException, UsernameTaken, TextTooLongException {
        switch (command.toLowerCase()) {
            case HELP:
                spacer(1);
                System.out.print("Lista dei comandi: ");
                for (String s : COMMANDS_LIST) {
                    System.out.print(s + " ");
                }
                spacer(1);
                break;
            case "/home":
                loadHomePage();
                break;
            case "/viewcampaigns":
                loadViewCampaigns();
                break;
            case "/createcampaign":
                loadCreateCampaigns();
                break;
            case "/viewpg", "/createpg":
                System.out.print("Non implementato");
                break;
            case "/login":
                loadLogin();
                break;
            case "/registration":
                loadRegistration();
                break;
            case "/logout":
                loadLogout();
                break;
            case "/viewnotifications":
                loadNotifications();
                break;
            case "/exit":
                execution = false;
                break;
            default:
                System.out.print("Inserisci un comando valido (scrivi /help per la lista con tutti i comandi)");
                break;
        }
        spacer(1);
    }

    public static void loadViewCampaigns () throws InvalidValueException, TextTooLongException {
        currentUser = SingletonLoggedUser.getInstance();

        if (currentUser == null || currentUser.getUserType() == null || currentUser.getUserType() == UserTypes.DM) {
            System.out.println("Devi essere loggato come Player per poter cercare e unirti alle campagne.");
            return;
        }

        CampaignParticipationControl viewCampaignsControl = new CampaignParticipationControl();
        List<BeanCampaign> allCampaigns = viewCampaignsControl.getAvailableCampaigns();

        if (allCampaigns == null || allCampaigns.isEmpty()) {
            System.out.println("Al momento non ci sono campagne disponibili.");
            return;
        }

        System.out.print("Vuoi filtrare la ricerca per nome, tipo o luogo? (s/n): ");
        String applyFilters = readInput().trim().toLowerCase();
        List<String> filters = applyFilterMethod(applyFilters);

        String nameFilter = filters.get(0);
        String typeFilter = filters.get(1);

        List<BeanCampaign> filteredCampaigns = searchMethod(allCampaigns, nameFilter, typeFilter);

        if (filteredCampaigns.isEmpty()) {
            System.out.println("Nessuna campagna corrisponde ai filtri inseriti.");
            return;
        }

        System.out.println("\n--- CAMPAGNE DISPONIBILI ---");
        UserDAO userDAO = DaoFactory.getFactory().createUserDAO();

        int selection = selectCampaign(filteredCampaigns, userDAO);

        if (selection == 0) {
            return;
        }

        BeanCampaign selectedCampaign = filteredCampaigns.get(selection - 1);

        System.out.print("Sei sicuro di voler mandare la richiesta di partecipazione a '" + selectedCampaign.getCampName() + "'? (s/n): ");
        String confirm = readInput().trim().toLowerCase();

        if (confirm.equals("s")) {
            BeanUser loggedBeanUser = new BeanUser();
            loggedBeanUser.setUserID(currentUser.getUserID());
            loggedBeanUser.setUsername(currentUser.getUsername());
            loggedBeanUser.setUserType(currentUser.getUserType());

            viewCampaignsControl.participate(selectedCampaign, loggedBeanUser);

            System.out.println("Richiesta di partecipazione inviata con successo al Master!");
        } else {
            System.out.println("Operazione annullata.");
        }
    }

    public static List<String> applyFilterMethod (String applyFilters){
        String nameFilter = "";
        String typeFilter = "";

        if (applyFilters.equals("s")) {
            System.out.print("Filtra per nome (lascia vuoto per ignorare): ");
            nameFilter = readInput().trim().toLowerCase();

            System.out.print("Filtra per tipo (online / offline / lascia vuoto per ignorare): ");
            typeFilter = readInput().trim().toLowerCase();

            while (!typeFilter.isEmpty() && !typeFilter.equals(ONLINE) && !typeFilter.equals(OFFLINE)) {
                System.out.print("Tipo non valido. Inserisci 'online', 'offline' o lascia vuoto: ");
                typeFilter = readInput().trim().toLowerCase();

            }


        }
        return Arrays.asList(nameFilter, typeFilter);
    }

    public static int selectCampaign (List < BeanCampaign > filteredCampaigns, UserDAO userDAO){
        for (int i = 0; i < filteredCampaigns.size(); i++) {
            BeanCampaign camp = filteredCampaigns.get(i);

            BeanUser master = new BeanUser(userDAO.retrieveUserByUserID(camp.getCampDMID()));
            String masterName = master.getUsername();


            String locationDisplay = camp.getCampMode() == Mode.ONLINE ? camp.getPlatform() : camp.getCampCity();

            System.out.println("[" + (i + 1) + "] Campagna: " + camp.getCampName() +
                    " | Master: " + masterName +
                    " | Tipo: " + camp.getCampMode() +
                    " | Luogo: " + locationDisplay);
        }

        System.out.println("[0] Torna indietro");
        int selection = -1;

        while (true) {
            System.out.print("\nSeleziona il numero della campagna a cui vuoi mandare la richiesta (0 per annullare): ");
            String input = readInput().trim();

            try {
                selection = Integer.parseInt(input);
                if (selection >= 0 && selection <= filteredCampaigns.size()) {
                    break;
                } else {
                    System.out.println("Errore: Inserisci un numero compreso tra 0 e " + filteredCampaigns.size() + ".");
                }
            } catch (NumberFormatException _) {
                System.out.println("Errore: Devi inserire un numero intero valido.");
            }
        }

        return selection;
    }

    public static List<BeanCampaign> searchMethod (List < BeanCampaign > allCampaigns, String nameFilter, String
            typeFilter){
        List<BeanCampaign> filteredCampaigns = new ArrayList<>();
        for (BeanCampaign camp : allCampaigns) {
            boolean matchName = nameFilter.isEmpty() || camp.getCampName().toLowerCase().contains(nameFilter);

            String campType = camp.getCampMode() == Mode.ONLINE ? ONLINE : OFFLINE;
            boolean matchType = typeFilter.isEmpty() || campType.equals(typeFilter);


            if (matchName && matchType) {
                filteredCampaigns.add(camp);
            }
        }
        return filteredCampaigns;
    }

    private static BeanCampaign buildCampaignData (String campaignName, String campaignType, String
            location, LocalDateTime dateTime, String freq,int maxPlayers){
        BeanCampaign campaignData = new BeanCampaign();

        campaignData.setCampName(campaignName);
        campaignData.setCampDate(dateTime);
        campaignData.setCampFreq(freq);
        campaignData.setMaxNumberOfPlayers(maxPlayers);

        switch (campaignType) {
            case OFFLINE:
                campaignData.setCampMode(Mode.OFFLINE);
                campaignData.setCampCity(location);
                break;
            case ONLINE:
                campaignData.setCampMode(Mode.ONLINE);
                campaignData.setPlatform(location);
                break;
            default:
                logger.log(Level.SEVERE, "Campaign type not found");
        }
        return campaignData;
    }

    public static void loadCreateCampaigns () {
        currentUser = SingletonLoggedUser.getInstance();
        if (currentUser == null || currentUser.getUserType() == null || currentUser.getUserType() == UserTypes.PLAYER) {
            System.out.print("Devi essere loggato come dm per farlo.");
            return;
        }

        System.out.print("Creando una campagna...\n" +
                "Inserisci nome campagna: ");

        List<String> list = chooseNameMode();
        String campaignName = list.get(0);
        String type = list.get(1);
        String location = list.get(2);


        ZonedDateTime zonedTime = ZonedDateTime.now(ZoneId.of("Europe/Rome"));
        LocalDate today = zonedTime.toLocalDate();
        LocalDate localDate = chooseDate(today);
        LocalTime localTime = chooseTime(today, localDate);
        String freq = chooseFreq();
        int maxNumberOfPlayers = chooseMaxPlayers();

        LocalDateTime dateTime = LocalDateTime.of(localDate, localTime);

        System.out.print("Inserisci l'username del giocatore da invitare (scrivi next una volta scelti tutti i giocatori): ");
        String invitingPlayer = readInput();
        List<BeanUser> invitingPlayers = new ArrayList<>();
        while (!invitingPlayer.equalsIgnoreCase("next")) {
            invitingPlayers = createCampaignControl.addNotifiedPlayer(invitingPlayers, invitingPlayer);
            System.out.print("Inserisci l'username del giocatore da invitare (scrivi next una volta scelti tutti i giocatori): ");
            invitingPlayer = readInput();
        }

        BeanCampaign campaign = buildCampaignData(campaignName, type, location, dateTime, freq, maxNumberOfPlayers);
        campaign.setDmId(currentUser.getUserID());
        campaign.setTimeSession(localTime);
        createCampaignControl.createCampaign(campaign);
        Integer campaignID = campaign.getCampId();
        createCampaignControl.notifyCreation(campaignID, invitingPlayers);

        System.out.println("\n[+] Campagna creata con successo!");

    }

    public static List<String> chooseNameMode () {
        String campaignName = readInput();
        while (campaignName.isEmpty()) {
            System.out.print("Nome non valido...\n" +
                    "Inserisci nome: ");
            campaignName = readInput();
        }

        System.out.print("Offline o online? ");
        String type = readInput().toLowerCase();
        while (!(type.equals(ONLINE) || type.equals(OFFLINE))) {
            System.out.print("Tipo non valido...\n" +
                    "Offline o online: ");
            type = readInput().toLowerCase();
        }

        String location = "";
        if (type.equals(OFFLINE)) {
            System.out.print("Inserisci città: ");
            location = readInput();
            while (location.isEmpty()) {
                System.out.print("Città non valida...\n" +
                        "Inserisci città: ");
                location = readInput();
            }
        } else {
            System.out.print("Inserisci piattaforma: ");
            location = readInput();
            while (location.isEmpty()) {
                System.out.print("Piattaforma non valida...\n" +
                        "Inserisci piattaforma: ");
                location = readInput();
            }
        }

        return Arrays.asList(campaignName, type, location);

    }

    public static LocalDate chooseDate (LocalDate today){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy"); // Corretto in MM numerico
        LocalDate localDate = null;
        boolean validDate = false;

        while (!validDate) {
            System.out.print("Inserisci data inizio (formato gg-mm-aaaa): ");
            String dateString = readInput();
            try {
                localDate = LocalDate.parse(dateString, formatter);
                if (localDate.isBefore(today)) {
                    System.out.println("Errore: La data non può essere nel passato.");
                } else {
                    validDate = true;
                }
            } catch (DateTimeParseException e) {
                System.out.println("Formato data non valido! Usa il formato gg-mm-aaaa (es. 24-12-2026).");
                log.error("e: ", e);
            }
        }
        return localDate;
    }

    public static LocalTime chooseTime (LocalDate today, LocalDate localDate){
        LocalTime localTime = null;
        ZonedDateTime zonedTime = ZonedDateTime.now(ZoneId.of("Europe/Rome"));
        LocalTime actualTime = zonedTime.toLocalTime();
        boolean validTime = false;

        while (!validTime) {
            System.out.print("Inserisci orario (formato oo:mm): ");
            String timeString = readInput();
            if (timeString.isEmpty()) {
                System.out.println("Errore: L'orario non può essere vuoto.\n");
                continue;
            }
            try {
                localTime = LocalTime.parse(timeString + ":00");
                // Se la data scelta è OGGI, l'orario deve essere nel futuro
                if (localDate.isEqual(today) && localTime.isBefore(actualTime)) {
                    System.out.println("Errore: L'orario per oggi è già passato...\n");
                } else {
                    validTime = true;
                }
            } catch (DateTimeParseException e) {
                System.out.println("Formato orario non valido: Usa il formato oo:mm (es. 14:30).\n");
                log.error("e: ", e);
            }
        }
        return localTime;
    }

    public static String chooseFreq () {
        System.out.print("Scegli frequenza sessioni (settimanale, bisettimanale, mensile): ");
        String freq = readInput().toLowerCase(); // Usato readInput()
        while (!(freq.equals("settimanale") || freq.equals("bisettimanale") || freq.equals("mensile"))) {
            System.out.print("Frequenza non valida...\n" +
                    "Scegli frequenza sessioni (settimanale, bisettimanale, mensile): ");
            freq = readInput().toLowerCase();
        }
        return freq;
    }

    public static int chooseMaxPlayers () {
        int maxNumberOfPlayers = 0;
        boolean validNumber = false;
        while (!validNumber) {
            System.out.print("Scegli numero massimo di giocatori (tra 1 e 10): ");
            String input = readInput(); // Usato readInput()
            try {
                maxNumberOfPlayers = Integer.parseInt(input);
                if (maxNumberOfPlayers >= 1 && maxNumberOfPlayers <= 10) {
                    validNumber = true;
                } else {
                    System.out.println("Numero massimo di giocatori non valido (Inserisci un valore tra 1 e 10)...\n");
                }
            } catch (NumberFormatException e) {
                System.out.println("Errore: Inserisci un numero intero valido...\n");
                log.error("e: ", e);
            }
        }
        return maxNumberOfPlayers;
    }

    public static void loadLogin () {
        boolean matchingData = false;

        while (!matchingData) {
            System.out.println("Scegli se accedere con il login standard (inserisci 1) o con Google (inserisci 2) : ");
            int choice = Integer.parseInt(readInput());
            if (choice == 2) {
                loadLoginGoogle();
                break;
            }
            System.out.print("Login: \n" +
                    "Inserisci username: ");
            String username = readInput();
            while (username.isEmpty()) {
                System.out.print("L''username non può essere vuoto...\n" +
                        "Inserisci username: ");
                username = readInput();
            }

            System.out.print(PASSWORD);
            String password = readInput();
            while (password.isEmpty()) {
                System.out.print("La password non può essere vuota...\n" +
                        PASSWORD);
                password = readInput();
            }

            if ((loginControl.loginStandard(username, password) != null)) {
                matchingData = true;
            }
        }

    }

    public static void loadLoginGoogle () {

        System.out.println("Login con Google...");

        //proviamo a recuperare il token
        try {
            String idToken = loginGoogleControl.startAuthGoogle();

            if (idToken == null || idToken.isEmpty()) {
                System.out.println("Login con Google fallito o annullato");
                return;
            }
            //prepariamo la bean per il login
            BeanUser beanUser = new BeanUser();
            beanUser.setIdToken(idToken);
            beanUser = loginControl.verifyLogin(beanUser, true);

            //Registrazione
            if (beanUser.getRegRequiredFlag() != null && beanUser.getRegRequiredFlag()) {
                System.out.println("Utente non registrato! Passiamo alla registrazione tramite Google: ");
                loadRegistrationGoogle(beanUser);
            } else {
                System.out.println("Login con Google effettuato con successo");
            }
        } catch (IllegalAccessException | IOException e) {
            System.out.println("Si è verificata un'eccezione durante il login con Google" + e.getMessage());
        }
    }

    public static void loadLogout () {
        System.out.print("Sicuro di voler effettuare il logout? (s/n) ");
        String logoutInput = readInput().trim().toLowerCase();
        while (!(logoutInput.equals("s") || logoutInput.equals("n"))) {
            System.out.print("Comando non valido,\n Sicuro di voler effettuare il logout? (s/n) ");
            logoutInput = readInput().trim().toLowerCase();
        }
        if (logoutInput.equals("s")) {
            loginControl.closeLoggedSession();
        }
    }

    public static void loadRegistration () throws InvalidValueException, TextTooLongException, UsernameTaken {
        System.out.println("Registrazione...");

        List<String> list = chooseUsernameEmail();
        String username = list.get(0);
        String email = list.get(1);
        String password = choosePassword();
        String type = chooseType();

        BeanUser beanUser = new BeanUser();
        beanUser.setUsername(username);
        beanUser.setEmail(email);
        beanUser.setPassword(password);
        if (UserTypes.PLAYER.toString().equalsIgnoreCase(type)) {
            beanUser.setUserType(UserTypes.PLAYER);
        } else {
            beanUser.setUserType(UserTypes.DM);
        }
        loginControl.completeRegistration(beanUser);
        System.out.print("Registrazione Completata");
    }

    public static List<String> chooseUsernameEmail () {
        String username;
        while (true) {
            System.out.print("Scegli username: ");
            username = readInput().trim();
            if (username.isEmpty()) {
                System.out.println("Errore: L'username non può essere vuoto.");
            } else if (loginControl.isUsernameTaken(username)) {
                System.out.println("Errore: Username già in uso.");
            } else {
                break;
            }
        }

        System.out.print("Scegli email: ");
        String email = readInput();
        while (email.isEmpty()) {
            System.out.print("L'email non può essere vuota...\n" +
                    "Inserisci email: ");
            email = readInput();
        }
        return Arrays.asList(username, email);
    }

    public static String choosePassword () {
        boolean matchingPasswords = false;
        String password = "";
        String confirmPassword = "";

        while (!matchingPasswords) {
            System.out.print(PASSWORD);
            password = readInput();
            while (password.isEmpty()) {
                System.out.print("La password non può essere vuota...\n" +
                        PASSWORD);
                password = readInput();
            }

            System.out.print("Conferma password: ");
            confirmPassword = readInput();
            while (confirmPassword.isEmpty()) {
                System.out.print("La conferma password non può essere vuota...\n" +
                        "Conferma password: ");
                confirmPassword = readInput();
            }

            if (confirmPassword.equals(password)) {
                matchingPasswords = true;
            } else {
                System.out.print("Le due password non coicidono\n");
            }
        }
        return password;
    }

    public static String chooseType () {
        String type;
        while (true) {
            System.out.print("Vuoi essere DM o Player? Attenzione: se scegli l'opzione Dungeon Master sarai in grado di creare ma non di partecipare a una campagna");
            type = readInput().trim().toLowerCase();
            if (type.isEmpty()) {
                System.out.println("Il tipo non può essere vuoto.");
            } else if (!(type.equals("dm") || type.equals("player"))) {
                System.out.println("Devi scegliere tra DM e Player");
            } else {
                break;
            }
            System.out.print("Vuoi essere DM o Player?");
        }

        return type;
    }

    public static void loadRegistrationGoogle (BeanUser bean){

        System.out.println("Registrazione con Google...");
        System.out.println("Il tuo username è: " + bean.getUsername());

        String type = chooseType();
        bean.setUserType(UserTypes.valueOf(type));

        try {
            loginGoogleControl.completeRegistrationGoogle(bean);
            System.out.println("Registrazione con Google completata con successo!");
        } catch (Exception e) {
            System.out.println("Errore durante la registrazione: " + e.getMessage());
        }

    }


    static final class MyNotifs {
        private List<String> titles;
        private List<String> descriptions;
        private List<NotificationTypes> types;

        public MyNotifs(List<String> titles, List<String> descriptions, List<NotificationTypes> types) {
            this.titles = titles;
            this.descriptions = descriptions;
            this.types = types;
        }

        public List<String> getTitles() {
            return titles;
        }

        public List<String> getDescriptions() {
            return descriptions;
        }

        public List<NotificationTypes> getType() {
            return types;
        }

    }

    private static MyNotifs loadPage (ArrayList < Notification > myNotifications) {
        int notifSize = myNotifications.size();
        List<String> titles = new ArrayList<>();
        List<String> descriptions = new ArrayList<>();
        List<NotificationTypes> types = new ArrayList<>();

        for (int i = 0; i < notifSize; i++) {
            Notification currentNotif = myNotifications.get(i);
            System.out.println(currentNotif.getCampaignID());
            CampaignDAO campaignDAO = DaoFactory.getFactory().createCampaignDAO();
            BeanCampaign beanCampaign = new BeanCampaign(campaignDAO.getCampaignById(currentNotif.getCampaignID()));
            String campaignName = beanCampaign.getCampName();

            UserDAO userDAO = DaoFactory.getFactory().createUserDAO();
            BeanUser beanUser = new BeanUser(userDAO.retrieveUserByUserID(currentNotif.getNotifierID()));
            String notifierName = beanUser.getUsername();

            String title = "";
            String description = "";

            switch (currentNotif.getNotificationType()) {
                case CAMPAIGN_ADDED:
                    title = "Campagna creata";
                    description = "La campagna " + campaignName + " è stata appena creata e sei stato invitato a parteciparvi.";
                    types.add(NotificationTypes.CAMPAIGN_ADDED);
                    break;
                case REQUEST_PARTICIPATION:
                    title = "Richiesta di partecipazione";
                    description = notifierName + " ha richiesto di partecipare alla tua campagna chiamata " + campaignName;
                    types.add(NotificationTypes.REQUEST_PARTICIPATION);
                    break;
                case ACCEPT_PARTICIPATION:
                    title = "Richiesta accettata";
                    description = notifierName + " ha accettato la tua richiesta di partecipazione alla campagna chiamata " + campaignName;
                    types.add(NotificationTypes.ACCEPT_PARTICIPATION);
                    break;
                default:
                    logger.log(Level.SEVERE, "Notification type error");
            }
            titles.add(title);
            descriptions.add(description);
        }
        return new MyNotifs(titles, descriptions, types);
    }


    public static void loadNotifications () {
        currentUser = SingletonLoggedUser.getInstance();
        if (currentUser == null || currentUser.getUserType() == null) {
            System.out.println("Devi effettuare il login per visualizzare le notifiche.");
            return;
        }

        ArrayList<Notification> myNotifications;
        NotificationDAO notificationdao = DaoFactory.getFactory().createNotificationDAO();
        int myId = currentUser.getUserID();
        myNotifications = notificationdao.getNotificationsByUserId(myId);

        MyNotifs notifsItems = loadPage(myNotifications);
        int notifSize = myNotifications.size();

        if (notifSize == 0) {
            System.out.println("Non hai nuove notifiche.");
            return;
        }

        System.out.println("--- LE TUE NOTIFICHE ---");
        for (int i = 0; i < notifSize; i++) {
            System.out.println("Notifica n. " + (i + 1) + ": " + notifsItems.getTitles().get(i));
            System.out.println(notifsItems.getDescriptions().get(i));
        }

        int notifNumber = chooseNotifNumber(notifSize);

        System.out.println("Hai selezionato la notifica all'indice: " + notifNumber);
        String readOrAccept;

        boolean actionCompleted = false;

        while (!actionCompleted) {
            switch (notifsItems.getType().get(notifNumber)) {
                case CAMPAIGN_ADDED, ACCEPT_PARTICIPATION:
                    System.out.print("Segnare come letto? (s/n): ");
                    readOrAccept = readInput().trim().toLowerCase();

                    if (readOrAccept.equals("s")) {
                        notificationControl.deleteNotification(myNotifications.get(notifNumber).getNotificationID());
                        System.out.println("Notifica cancellata.");
                        actionCompleted = true;
                    } else if (readOrAccept.equals("n")) {
                        actionCompleted = true;
                    } else {
                        System.out.println("Errore: risposta non valida.");
                    }
                    break;

                case REQUEST_PARTICIPATION:
                    System.out.print("Vuoi accettare questo utente? (digita s/n per accettarlo/rifiutarlo o 'indietro'): ");
                    readOrAccept = readInput().trim().toLowerCase();
                    actionCompleted = manageUserRequest(readOrAccept, notifNumber, myNotifications);
                    break;

                default:
                    logger.log(Level.SEVERE, "Notification type error");
                    actionCompleted = true;
                    break;
            }
        }
    }

    public static int chooseNotifNumber ( int notifSize){
        int notifNumber;
        String input;
        while (true) {
            System.out.print("Seleziona il numero della notifica da aprire (1 - " + notifSize + "): ");
            input = readInput().trim();

            try {
                notifNumber = Integer.parseInt(input);

                if (notifNumber >= 1 && notifNumber <= notifSize) {
                    notifNumber = notifNumber - 1;
                    break;
                } else {
                    System.out.println("Errore: Inserisci un numero compreso tra 1 e " + notifSize + ".");
                }

            } catch (NumberFormatException e) {
                System.out.println("Errore: Devi inserire un numero intero valido.");
                log.error("e: ", e);
            }
        }
        return notifNumber;
    }

    public static boolean manageUserRequest (String readOrAccept,int notifNumber, List<
            Notification > myNotifications){
        boolean actionCompleted = false;
        Notification currentNotif = myNotifications.get(notifNumber);
        ManageRequestControl manageRequestControl = new ManageRequestControl();
        if (readOrAccept.equals("s")) {

            BeanCampaign beanCampaign = populateBeanCampaign(currentNotif);
            BeanUser beanUser = populateBeanUser(currentNotif);

            boolean accepted = manageRequestControl.acceptPlayer(beanCampaign, beanUser);
            if(!accepted){
                logger.log(Level.WARNING, "Can't accept player. Check if campaign is full");
                return false;
            }
            notificationControl.deleteNotification(myNotifications.get(notifNumber).getNotificationID());
            System.out.println("Richiesta accettata.");
            actionCompleted = true;
        } else if (readOrAccept.equals("n")) {
            BeanCampaign beanCampaign = populateBeanCampaign(currentNotif);
            BeanUser beanUser = populateBeanUser(currentNotif);
            boolean rejected = manageRequestControl.rejectPlayer(beanCampaign, beanUser);
            if(!rejected){
                logger.log(Level.WARNING, "Can't reject player");
                return false;
            }
            notificationControl.deleteNotification(myNotifications.get(notifNumber).getNotificationID());
            System.out.println("Richiesta rifiutata.");
            actionCompleted = true;
        } else if (readOrAccept.equals("indietro")) {
            actionCompleted = true;
        } else {
            System.out.println("Errore: risposta non valida.");
        }
        return actionCompleted;
    }


    public static class CommandSignalException extends RuntimeException {
        private final String command;

        public CommandSignalException(String command) {
            this.command = command;
        }

        public String getCommand() {
            return command;
        }
    }

    public static BeanCampaign populateBeanCampaign(Notification currentNotif){
            CampaignDAO campaignDAO = DaoFactory.getFactory().createCampaignDAO();
            ModelCampaign campaign  = campaignDAO.getCampaignById(currentNotif.getCampaignID());
        return new BeanCampaign(campaign);

    }

    public static BeanUser populateBeanUser(Notification currentNotif){
        UserDAO userDAO = DaoFactory.getFactory().createUserDAO();
        User user = userDAO.retrieveUserByUserID(currentNotif.getNotifierID());
        return new BeanUser(user);

    }

}