package logic;


import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import logic.view.CLI;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Main extends Application {
    private static final Logger logger = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) {

        if (args.length > 0 && "--cli".equalsIgnoreCase(args[0])) {
            // avviamo la modalità CLI
            logger.log(Level.INFO, "Starting CLI mode...");
            CLI.main(new String[0]);
        } else {
            //avviamo la modalità GUI
            logger.log(Level.INFO, "Starting GUI mode...");
            launch(args);
        }
    }

    @Override
    public void start(Stage stage) throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/InitialGUI.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1280, 760);
        stage.setScene(scene);
        stage.show();

    }

    }












