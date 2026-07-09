package logic.controllers;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import com.google.api.client.googleapis.auth.oauth2.*;
import com.google.api.client.http.HttpTransport;
import logic.beans.BeanUser;
import logic.controllers.abstract_factory_dao.DaoFactory;
import logic.dao.UserDAO;
import logic.exceptions.InvalidValueException;
import logic.exceptions.TextTooLongException;
import logic.model.ModelCampaign;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.services.calendar.Calendar;
import com.google.api.client.auth.oauth2.Credential;
import java.util.Arrays;
import java.util.List;
import com.google.api.client.util.store.FileDataStoreFactory;
import java.io.File;

import java.awt.*;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.logging.Level;
import java.util.logging.Logger;

import logic.model.User;
import logic.utils.SingletonLoggedUser;


public class LoginGoogleControl {

    private static final Logger logger = Logger.getLogger(LoginGoogleControl.class.getName());

    private UserDAO daoUser;
    private ModelCampaign modelCamp;
    LoginGoogleControl googleController;
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance(); //classe factory che configura e crea istanze di parser e generatori (leggere e scrivere JSON)

    private static final HttpTransport transport = new com.google.api.client.http.javanet.NetHttpTransport();

    public LoginGoogleControl() {
        this.daoUser = DaoFactory.getFactory().createUserDAO();
        this.modelCamp = new ModelCampaign();
    }

    // verifichiamo l'ID token
    private String verifyGoogleToken(String idTokenStr) throws GeneralSecurityException, IOException {
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(transport, JSON_FACTORY)
                .setAudience(Collections.singletonList("814237367403-24vnr168a0ov88ee6r517ih1f4iees15.apps.googleusercontent.com"))
                .build();
        GoogleIdToken idToken = verifier.verify(idTokenStr);
        if (idToken != null) {
            GoogleIdToken.Payload payload = idToken.getPayload(); //contenuto decodificato del GoogleId token
            return payload.getEmail();
        }
        return null;
    }

    private BeanUser createNewUserBean(String email, String username) {
        try {
            BeanUser bean = new BeanUser();
            bean.setEmail(email);
            bean.setUsername(username);
            bean.setRegRequiredFlag(true); // da qui la view chiama completeRegistration
            return bean;
        } catch (TextTooLongException | InvalidValueException e) {
            logger.log(Level.WARNING, "Something went wrong while setting the username", e);
            return null;
        }
    }

    public BeanUser loginWithGoogle(String idTokenStr) {
        try {
            String email = verifyGoogleToken(idTokenStr);
            if (email == null) {
                logger.log(Level.WARNING, "Token not valid or email does not exist");
                return null;
            }

            String username = email.split("@")[0];

            if (!daoUser.existenceUser(email)) {
                logger.log(Level.INFO, "User does not exist. Going to registration");
                return createNewUserBean(email, username);
            }

            User user = daoUser.verifyLogin(email, null, true); //utente già registrato

            if (user != null) {
                initLoggedSession(user);
                BeanUser existingUser = new BeanUser(user);
                existingUser.setRegRequiredFlag(false);
                return existingUser;
            }

        } catch (GeneralSecurityException e) {
            logger.log(Level.SEVERE, "GeneralSecurityException occurred in login with Google", e);
        } catch (IOException ex) {
            logger.log(Level.SEVERE, "IOException occurred in login with Google", ex);
        }

        return null;
    }

    public void completeRegistrationGoogle(BeanUser bean) {
        User user = new User();
        user.setUserType(bean.getUserType());
        user.setUsername(bean.getUsername());
        user.setEmail(bean.getEmail());
        user.setPassword(null);

        daoUser.registerUser(user);
        User registeredUser = daoUser.retrieveUserByUsername(user.getUsername()); //in tal modo sono sicura di avere il vero ID
        initLoggedSession(registeredUser);
    }

    private void initLoggedSession(User user) {
        SingletonLoggedUser session = SingletonLoggedUser.getInstance();
        session.setUserID(user.getUserID());
        session.setUsername(user.getUsername());
        session.setUserType(user.getUserType());
        logger.log(Level.INFO, "Session created for user: {0}", user.getUsername());
    }

    public void closeLoggedSession() {
        SingletonLoggedUser.getInstance().cleanSession();
        logger.log(Level.INFO, "Session closed successfully");
    }

    public String startAuthGoogle() throws IOException, IllegalAccessException {
        //estraiamo il contenuto del file json che contiene i dati necessari
        GoogleClientSecrets secrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader
                (getClass().getResourceAsStream("/client_secret.json")));

        // Creiamo la lista dei permessi (Email + Calendario)
        List<String> scopes = Arrays.asList(
                "https://www.googleapis.com/auth/userinfo.email",
                "https://www.googleapis.com/auth/calendar.events"
        );

        //gestisce e mantiene le credenziali degli end-users
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(transport,
                JSON_FACTORY, secrets, scopes).setDataStoreFactory(new FileDataStoreFactory(new File("tokens"))).setAccessType("offline").build();

        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888)
                .setCallbackPath("/Callback")
                .build();

        try {
            String redirectUri = receiver.getRedirectUri();
            String authorization = flow.newAuthorizationUrl().setRedirectUri(redirectUri).build();

            //apriamo il browser
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                Desktop.getDesktop().browse(URI.create(authorization));
            } else {
                throw new IllegalAccessException("Can't open browser");
            }

            String code = receiver.waitForCode();

            GoogleTokenResponse token = flow.newTokenRequest(code).setRedirectUri(redirectUri).execute();


            flow.createAndStoreCredential(token, "user");

            return token.getIdToken();

        } finally {
            receiver.stop();
        }
    }

    public Calendar getCalendarService() throws IOException {
        // Ricarichiamo il file client_secret
        GoogleClientSecrets secrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(
                getClass().getResourceAsStream("/client_secret.json")));

        // Dobbiamo usare la stessa identica lista di permessi usata nel login
        List<String> scopes = Arrays.asList(
                "https://www.googleapis.com/auth/userinfo.email",
                "https://www.googleapis.com/auth/calendar.events"
        );

        // Ricostruiamo il flusso
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                transport, JSON_FACTORY, secrets, scopes).setDataStoreFactory(new FileDataStoreFactory(new File("tokens"))).setAccessType("offline")
                .build();

        // Recuperiamo la credenziale salvata dal metodo startAuthGoogle()
        Credential credential = flow.loadCredential("user");

        if (credential == null) {
            logger.log(Level.WARNING, "Nessuna credenziale Google trovata. L'utente ha effettuato il login?");
            return null;
        }


        return new Calendar.Builder(transport, JSON_FACTORY, credential)
                .setApplicationName("World Serpent Inn")
                .build();
    }

}