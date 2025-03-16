package focusApp.controllers;

import focusApp.HelloApplication;
import focusApp.database.UserDAO;
import focusApp.models.user.User;
import focusApp.models.colour.UserConfig;
import focusApp.models.user.UserHolder;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.IOException;
import java.util.Objects;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class LoginController {
    @FXML
    public Button backButton;
    @FXML
    public Button loginButton;
    @FXML
    public Button confirmButton;
    @FXML
    public TextField userNameTextField;
    @FXML
    public PasswordField passwordTextField;
    @FXML
    public TextField regUserNameTextField;
    @FXML
    public PasswordField regPasswordTextField;
    @FXML
    public PasswordField confirmPasswordTextField;
    @FXML
    public Label denyLoginLabel;
    @FXML
    public Label denyRegisterLabel;
    @FXML
    public Hyperlink regLink;
    @FXML
    public Hyperlink loginLink;

    /**
     * singleton used to hold user class for use in other controllers
     */
    private UserHolder userHolder = UserHolder.getInstance();

    /**
     * Initialise the controller automatically after the FXML file is loaded
     */
    @FXML
    private void initialize(){}

    /**
     * Handles the onBackButtonClick action by loading the
     * home-view FXML and the appropriate stylesheet
     * @throws IOException
     *      If an exception occurred while loading the FXML file
     */
    @FXML
    protected void onBackButtonClick() throws IOException {
        Stage stage = (Stage) backButton.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("fxml/home-view.fxml"));
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
     * Handles the onLoginButtonClick action.
     * If the username and password provided are correct,
     * the main-view FXML and the appropriate stylesheet are loaded
     * Otherwise, an error message is displayed
     *
     * @throws IOException
     *      If an exception occurred while loading the FXML file
     */
    @FXML
    protected void onLoginButtonClick() throws IOException {
        /* create userDAO and attempt to log in */
        UserDAO userDAO = new UserDAO();
        User user = userDAO.login(userNameTextField.getText(), passwordTextField.getText());

        /* if user != null then login successful and user class returned */
        if (user != null){
            userHolder.setUser(user);
            Stage stage = (Stage) loginButton.getScene().getWindow();
            FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("fxml/main-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), HelloApplication.WIDTH, HelloApplication.HEIGHT);

            // Set scene stylesheet
            if (UserConfig.FindCSSFile()){
                scene.getStylesheets().add(UserConfig.getCSSFilePath().toUri().toString());
            } else {
                scene.getStylesheets().add(Objects.requireNonNull(HelloApplication.class.getResource("stylesheet.css")).toExternalForm());
            }
            stage.setScene(scene);
        } else {
            denyLoginLabel.setText("* Incorrect username or password. *");
        }
    }

    /**
     * Handles the onConfirmButtonClick action.
     * If inputted passwords match and are not empty,
     * and if username is available, it attempts to
     * create a new user account.
     * If creation is successful,
     * the username and password provided are correct,
     * the login-view FXML and the appropriate stylesheet are loaded
     * Otherwise, an error message is displayed
     *
     * @throws IOException
     *      If an exception occurred while loading the FXML file
     */
    @FXML
    protected void onConfirmButtonClick() throws IOException {
        /* check two inputted passwords are same */
        if (!Objects.equals(regPasswordTextField.getText(), confirmPasswordTextField.getText())) {
            denyRegisterLabel.setText("* Passwords don't match. *");
            return;
        } else if (regPasswordTextField.getLength() == 0 || regUserNameTextField.getLength() == 0) {
            denyRegisterLabel.setText("* password and username must not be 0 characters *");
            return;
        }

        /* create userDAO and attempt to create account */
        UserDAO userDAO = new UserDAO();
        User user = userDAO.addUser(regUserNameTextField.getText(), regPasswordTextField.getText());

        if (user != null) {
            userHolder.setUser(user);

            Stage stage = (Stage) confirmButton.getScene().getWindow();
            FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("fxml/login-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), HelloApplication.WIDTH, HelloApplication.HEIGHT);

            // Set scene stylesheet
            if (UserConfig.FindCSSFile()){
                scene.getStylesheets().add(UserConfig.getCSSFilePath().toUri().toString());
            } else {
                scene.getStylesheets().add(Objects.requireNonNull(HelloApplication.class.getResource("stylesheet.css")).toExternalForm());
            }
            stage.setScene(scene);
        } else {
            denyRegisterLabel.setText("This username is already taken");
        }
    }

    /**
     * Handles the onRegisterLinkClick action by loading the
     * register-view FXML and the appropriate stylesheet
     * @throws IOException
     *      If an exception occurred while loading the FXML file
     */
    @FXML
    protected void onRegisterLinkClick() throws IOException{
        Stage stage = (Stage) regLink.getScene().getWindow();
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
     * Handles the onLoginLinkClick action by loading the
     * login-view FXML and the appropriate stylesheet
     * @throws IOException
     *      If an exception occurred while loading the FXML file
     */
    @FXML
    protected void onLoginLinkClick() throws IOException{
        Stage stage = (Stage) loginLink.getScene().getWindow();
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
}