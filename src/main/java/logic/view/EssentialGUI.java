package logic.view;

import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import logic.utils.*;
import logic.utils.enums.UserTypes;
import javafx.animation.PauseTransition;
import javafx.scene.control.Label;

public class EssentialGUI extends Application {

    protected static Scene scene;
    private static final String APP_NAME = "WorldSerpentInn";
    private static final String PATH = "icons";
    private static final String LOGO_NAME = "imm.png";
    private static final String LABEL_COLOR_GREEN = "green";
    private static final String LABEL_COLOR_RED = "red";
    protected static String sceneName;

    private static final Logger logger = Logger.getLogger(EssentialGUI.class.getName());

    @Override
    public void start(Stage stage) {
        try {
            stage.setTitle(APP_NAME);

            // Inserisce l'icona solo se non è già presente, evitando di sovrapporle a ogni cambio scena
            if (stage.getIcons().isEmpty()) {
                String absolutePath = setAbsolutePath();
                Image logoImage = new Image(absolutePath);
                stage.getIcons().add(logoImage);
            }

            stage.setResizable(false);
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Errore fatale nell'avvio della finestra (start)", e);
        }
    }

    @FXML
    protected javafx.scene.control.MenuButton userMenuButton;

    public EssentialGUI() {
        javafx.application.Platform.runLater(() -> {
            if (this.userMenuButton != null) {
                SingletonLoggedUser currentUser = SingletonLoggedUser.getInstance();
                if (currentUser != null && currentUser.getUserID() != -1) {
                    this.userMenuButton.setText(currentUser.getUsername());
                } else {
                    this.userMenuButton.setText("Ospite");
                }
            }
        });
    }

    public void changeGUI(ActionEvent event, String newScene) {
        try {
            setScene(newScene);
            // Se loadApp fallisce, l'eccezione salta direttamente al catch, evitando il congelamento
            loadApp();
            nextGuiOnClick(event);
        } catch (Exception e) {
            logger.log(Level.SEVERE, e, () -> "Impossibile cambiare GUI verso: " + newScene); //concatenazione di stringhe dentro lambda
        }
    }

    public void nextGuiOnClick(ActionEvent event) {
        Object source = event.getSource();
        Stage next = null;

        try {
            if (source instanceof Node) {
                next = (Stage) ((Node) source).getScene().getWindow();
            } else if (source instanceof javafx.scene.control.MenuItem) {
                javafx.scene.control.MenuItem menuItem = (javafx.scene.control.MenuItem) source;
                if (menuItem.getParentPopup() != null) {
                    next = (Stage) menuItem.getParentPopup().getOwnerWindow();
                }
            }

            if (next != null) {
                start(next);
            } else {
                logger.log(Level.SEVERE, "Impossibile recuperare lo Stage: elemento non supportato.");
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Errore durante il recupero dello Stage per il cambio GUI", e);
        }
    }


    private static void loadApp() throws Exception {
        String path = sceneName.startsWith("/") ? sceneName : "/view/" + sceneName;
        URL loc = EssentialGUI.class.getResource(path);

        if (loc == null) {
            throw new IOException("File FXML non trovato al percorso: " + path);
        }

        Parent root = FXMLLoader.load(loc);
        scene = new Scene(root);

        URL cssLoc = EssentialGUI.class.getResource("/view/application.css");
        if (cssLoc != null) {
            scene.getStylesheets().add(cssLoc.toExternalForm());
        } else {
            logger.log(Level.WARNING, "File CSS 'application.css' non trovato, continuo senza stile.");
        }
    }

    private static void setScene(String newScene) {
        sceneName = newScene;
    }

    public void showHomePage(ActionEvent event){
        if(SingletonLoggedUser.getInstance().getUserID() == -1){
            changeGUI(event, "InitialGUI.fxml");
        }else if (SingletonLoggedUser.getInstance().getUserType().equals(UserTypes.PLAYER) || SingletonLoggedUser.getInstance().getUserType().equals(UserTypes.DM)) {
            changeGUI(event, "InitialGUILogged.fxml");
        }
    }

    public void showShowCampaigns(ActionEvent event) {
        SingletonLoggedUser currentUser = SingletonLoggedUser.getInstance();

        if (currentUser != null && currentUser.getUserType() != null) {
            if (currentUser.getUserType() == UserTypes.PLAYER) {
                changeGUI(event, "viewCampaigns.fxml");
            } else {
                showSuccessBanner("DM");
            }
        } else {
            showSuccessBanner("Log");
        }
    }

    public void showShowPg(){
        //empty
    }

    public void loadNotifications(ActionEvent event){
        SingletonLoggedUser currentUser = SingletonLoggedUser.getInstance();

        if (currentUser != null && currentUser.getUserType() != null) {
            changeGUI(event, "notifications.fxml");
        } else {
            showSuccessBanner("Log");
        }
    }

    public void showCreateCampaign(ActionEvent event) {
        SingletonLoggedUser currentUser = SingletonLoggedUser.getInstance();

        if (currentUser != null && currentUser.getUserType() != null) {
            if (currentUser.getUserType() == UserTypes.DM) {
                changeGUI(event, "createCampaign.fxml");
            } else {
                showSuccessBanner("Player");
            }
        } else {
            showSuccessBanner("Log");
        }
    }

    public void showCreatePg(){
        //empty
    }

    public void handleLogout(ActionEvent event) {
        SingletonLoggedUser.getInstance().setUserID(-1);
        SingletonLoggedUser.getInstance().setUsername(null);
        SingletonLoggedUser.getInstance().setUserType(null);

        changeGUI(event, "InitialGUI.fxml");

        showSuccessBanner("logout");
    }

    private String setAbsolutePath() {
        try {
            return getClass().getResource("/" + PATH + "/" + LOGO_NAME).toExternalForm();
        } catch (NullPointerException _) {
            throw new NullPointerException();
        }
    }

    public static void showSuccessBanner(String actionType) {
        javafx.application.Platform.runLater(() -> {
            Scene targetScene = null;
            for (javafx.stage.Window window : javafx.stage.Window.getWindows()) {
                if (window.isShowing() && window.getScene() != null) {
                    targetScene = window.getScene();
                    break;
                }
            }

            if (targetScene != null && targetScene.getRoot() instanceof javafx.scene.layout.AnchorPane) {
                javafx.scene.layout.AnchorPane root = (javafx.scene.layout.AnchorPane) targetScene.getRoot();
                String message;
                String labelColor;

                switch (actionType.toLowerCase()) {
                    case "login":
                        message = "Login effettuato";
                        labelColor = LABEL_COLOR_GREEN;
                        break;
                    case "logout":
                        message = "Logout effettuato";
                        labelColor = LABEL_COLOR_GREEN;
                        break;
                    case "registrazione":
                        message = "Registrazione completata";
                        labelColor = LABEL_COLOR_GREEN;
                        break;
                    case "dm":
                        message = "Devi essere loggato come Player per farlo";
                        labelColor = LABEL_COLOR_RED;
                        break;
                    case "player":
                        message = "Devi essere loggato come DM per farlo";
                        labelColor = LABEL_COLOR_RED;
                        break;
                    case "log":
                        message = "Devi essere loggato per farlo";
                        labelColor = LABEL_COLOR_RED;
                        break;
                    default:
                        Logger.getLogger(EssentialGUI.class.getName()).log(Level.WARNING, "Azione non riconosciuta: {0}", actionType);
                        return;
                }

                Label notification = new Label(message);
                notification.setStyle(
                        "-fx-background-color: " + labelColor + "; " +
                                "-fx-text-fill: white; " +
                                "-fx-padding: 10px 20px; " +
                                "-fx-font-size: 16px; " +
                                "-fx-font-weight: bold; " +
                                "-fx-background-radius: 5px; " +
                                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.4), 10, 0, 0, 5);"
                );

                javafx.scene.layout.StackPane bannerContainer = new javafx.scene.layout.StackPane(notification);
                bannerContainer.setMouseTransparent(true);

                javafx.scene.layout.AnchorPane.setTopAnchor(bannerContainer, 20.0);
                javafx.scene.layout.AnchorPane.setLeftAnchor(bannerContainer, 0.0);
                javafx.scene.layout.AnchorPane.setRightAnchor(bannerContainer, 0.0);

                root.getChildren().add(bannerContainer);
                bannerContainer.toFront();

                // Rimuove il banner dopo 3 secondi
                PauseTransition delay = new PauseTransition(javafx.util.Duration.seconds(3));
                delay.setOnFinished(e -> root.getChildren().remove(bannerContainer));
                delay.play();

            } else {
                Logger.getLogger(EssentialGUI.class.getName()).log(Level.SEVERE, "Impossibile disegnare il banner: la radice non è un AnchorPane.");
            }
        });
    }
}