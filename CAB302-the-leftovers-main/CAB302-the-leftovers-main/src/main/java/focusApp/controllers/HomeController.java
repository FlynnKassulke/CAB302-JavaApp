package focusApp.controllers;

import focusApp.HelloApplication;
import focusApp.models.colour.UserConfig;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;


import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class HomeController {

    public Button loginButton;
    public Button registerButton;
    @FXML
    private ImageView focusAppLogo;

    /**
     * Initialise the controller automatically after the FXML file is loaded
     */
    @FXML
    private void initialize() {

    }

    /**
     * Handles the onRegisterButtonClick action by loading the
     * register-view FXML and the appropriate stylesheet
     *
     * @throws IOException
     *      If an exception occurred while loading the FXML file
     */
    @FXML
    protected void onRegisterButtonClick() throws IOException {
        Stage stage = (Stage) registerButton.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("fxml/register-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), HelloApplication.WIDTH, HelloApplication.HEIGHT);

        // Set scene stylesheet
        if (UserConfig.FindCSSFile()){
            scene.getStylesheets().add(UserConfig.getCSSFilePath().toUri().toString());
        } else {
            scene.getStylesheets().add(Objects.requireNonNull(HelloApplication.class.getResource("stylesheet.css")).toExternalForm());
        }
        stage.setScene(scene);
    }

    /**
     * Handles the onLoginButtonClick action by loading the
     * login-view FXML and the appropriate stylesheet
     *
     * @throws IOException
     *      If an exception occurred while loading the FXML file
     */
    @FXML
    protected void onLoginButtonClick() throws IOException {
        Stage stage = (Stage) loginButton.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("fxml/login-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), HelloApplication.WIDTH, HelloApplication.HEIGHT);

        // Set scene stylesheet
        if (UserConfig.FindCSSFile()){
            scene.getStylesheets().add(UserConfig.getCSSFilePath().toUri().toString());
        } else {
            scene.getStylesheets().add(Objects.requireNonNull(HelloApplication.class.getResource("stylesheet.css")).toExternalForm());
        }
        stage.setScene(scene);
    }

    /**
     * Constructs new HomeController when initialised
     */
    public HomeController(){
    }

}