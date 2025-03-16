package focusApp;
import java.io.IOException;
import java.util.Objects;

import focusApp.models.timer.Timer;
import focusApp.models.colour.UserConfig;
import fr.brouillard.oss.cssfx.CSSFX;
import fr.brouillard.oss.cssfx.api.URIToPathConverter;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;


public class HelloApplication extends Application {

    // Constants defining the window title and size
    public static final String TITLE = "Hyper Focus";
    public static final int WIDTH = 390;
    public static final int HEIGHT = 600;


    /**
     * Sets the configurations of the home page
     *
     * @param stage
     *      The primary stage for HyperFocus that scenes can be set on
     * @throws IOException
     *      If an exception occurred while loading the FXML file
     */
    @Override
    public void start(Stage stage) throws IOException {
        // Load fxml file
        FXMLLoader root = new FXMLLoader(HelloApplication.class.getResource("fxml/home-view.fxml"));

        // Set scene
        Scene scene = new Scene(root.load(), WIDTH, HEIGHT);

        // Set scene stylesheet
        if (UserConfig.FindCSSFile()){
            scene.getStylesheets().add(UserConfig.getCSSFilePath().toUri().toString());
        } else {
            UserConfig.SetUpEnvironment();
            scene.getStylesheets().add(Objects.requireNonNull(HelloApplication.class.getResource("stylesheet.css")).toExternalForm());
        }

        URIToPathConverter myConverter = uri -> UserConfig.getCSSFilePath();
        CSSFX.addConverter(myConverter).start();

        // Set application icon
        stage.getIcons().add(new Image(Objects.requireNonNull(HelloApplication.class.getResourceAsStream("images/logoSmall.png"))));

        // Add scene to stage
        stage.setScene(scene);

        // Set window properties
        stage.setTitle(TITLE);      // Set window title
        stage.setResizable(false);  // User cannot resize window
        stage.setX(1130);           // Set X position of window
        stage.setY(280);            // Set Y position of window

        // Show stage
        stage.setOnCloseRequest(windowEvent -> Timer.ForceStopTimer());
        stage.show();
    }

    /**
     * Launches the application
     * @param args
     *      Command line arguments
     */
    public static void main(String[] args) {
        launch();
    }
}