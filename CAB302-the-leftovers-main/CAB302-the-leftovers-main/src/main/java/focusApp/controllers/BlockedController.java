package focusApp.controllers;

import focusApp.HelloApplication;

import focusApp.database.*;
import focusApp.database.PresetDAO;
import focusApp.database.WebsiteDAO;
import focusApp.database.ApplicationDAO;
import focusApp.database.Preset;

import focusApp.models.block.ApplicationItem;
import focusApp.models.block.BlockedItem;
import focusApp.models.block.WebsiteItem;
import focusApp.models.colour.UserConfig;
import focusApp.models.preset.PresetHolder;
import focusApp.models.user.User;
import focusApp.models.user.UserHolder;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import javafx.scene.input.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;

import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.Imaging;
import org.apache.commons.io.FileUtils;



//Jsoup
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.imageio.ImageIO;
import javax.swing.*;

import java.util.regex.Pattern;
import java.util.regex.Matcher;


public class BlockedController implements Initializable {




    public StackPane turnOffParentalControlsStackPane;
    public PasswordField parentalControlsPasswordField;
    public Label denyParentalControlsDisableLabel;

    private String fileLocation;
    public StackPane addWebsiteStackPane;
    public TextField addWebsiteTextField;
    public StackPane addApplicationStackPane;
    public TextField addApplicationTextField;
    public Button addWebButton;
    public Button addAppButton;

    boolean changesSaved = true;
    public Button saveButton;
    public Button cancelButton;

    public StackPane blackOutStackPane;
    public StackPane confirmCancelStackPane;



    @FXML
    private TableColumn<BlockedItem, String> iconColumn;

    @FXML
    private TableColumn<BlockedItem, String> nameColumn;

    @FXML
    private TableColumn<BlockedItem, String> locationColumn;

    @FXML
    private TableView<BlockedItem> tableView;

    private IBlockedItemDAO blockedDAO;
    private PresetDAO presetDAO;
    private ApplicationDAO applicationDAO;
    private WebsiteDAO websiteDAO;
    private UserDAO userDAO;
    private PresetHolder presetHolder;
    private Preset currentPreset;
    private UserHolder userHolder;
    private User user;
    private static ArrayList<BlockedItem> blockedItems;

    /**
     * Constructs new BlockedController and initialises necessary
     * components
     */
    public BlockedController(){
        blockedDAO = new MockedBlockedItemDAO();
        presetDAO = new PresetDAO();
        applicationDAO = new ApplicationDAO();
        websiteDAO = new WebsiteDAO();
        userHolder = UserHolder.getInstance();
        presetHolder = PresetHolder.getInstance();
        blockedItems = new ArrayList<BlockedItem>();
        userDAO = new UserDAO();

        /* only using one preset per user for now */

        /* get preset for user and if none create new preset */

        /* check if user logged in */
        user = userHolder.getUser();
        if (user == null) {
            throw new Error("User is null");
        }

        currentPreset = presetHolder.getPreset();
        if (currentPreset == null) {
            throw new Error("Preset is null");
        }

        /* get all presets and if a preset exists use that otherwise create new preset */

        /* populate blockedItems list from preset */
        blockedItems.addAll(presetDAO.getPresetWebsite(currentPreset.getPresetID()));
        blockedItems.addAll(presetDAO.getPresetApplication(currentPreset.getPresetID()));
    }


    /**
     * Extracts image URL, name, and link from the given website
     * @param newWebsiteURL
     *      The URL of the website
     */
    @FXML
    private void addNewWebsite(String newWebsiteURL)
    {
        String[] info = getWebPageTitleAndImage(newWebsiteURL);
        WebsiteItem websiteItem = websiteDAO.addWebsite(info[1], info[2], info[0]);
//        blockedDAO.addApplication((new BlockedApplication(info[0], info[1], info[2])));
//        syncBlockedApplications();

        if (websiteItem == null) {
            websiteItem = websiteDAO.getWebsite(websiteDAO.getWebsiteID(info[1]));

            System.out.println("Website already exists adding to preset.");
        } else {
            /* logging */
            System.out.println("Image URL: " + info[0]);
            System.out.println("Name: " + info[1]);
            System.out.println("Web/File Link: " + info[2]);
        }

        System.out.println("Image URL: " + info[0]);
        System.out.println("Name: " + info[1]);
        System.out.println("Web/File Link: " + info[2]);

        blockedItems.add(websiteItem);

        syncBlockedApplications();
    }

    /**
     * Extracts the image URL, name, and link from the application location
     * @param newApplicationLocation
     *      The location of the application
     */
    @FXML
    private void addNewApplication(String newApplicationLocation)
    {
        String[] info = getExecutableTitleAndIcon(newApplicationLocation);
        ApplicationItem applicationItem = applicationDAO.addApplication(info[1], info[2], info[0]);
//        blockedDAO.addApplication((new BlockedApplication(info[0], info[1], info[2])));
//        syncBlockedApplications();

        if (applicationItem == null) {
            applicationItem = applicationDAO.getApplication(applicationDAO.getApplicationID(info[1]));
        }

        System.out.println("Image URL: " + info[0]);
        System.out.println("Name: " + info[1]);
        System.out.println("Web/File Link: " + info[2]);

        blockedItems.add(applicationItem);

        syncBlockedApplications();
    }

    /**
     * Retrieves the webpage title and the first image source URL.
     * @param url The URL of the webpage.
     * @return A string array where the first element is the title and the second is the image URL.
     */
    public static String[] getWebPageTitleAndImage(String url) {
        try {
            Document doc = Jsoup.connect(url).get();

            // Get the title of the webpage
            String title = doc.title();

            // Attempt to get the URL of the favicon
            String faviconUrl = "No favicon found";
            Elements links = doc.head().select("link[href]");
            for (Element link : links) {
                String relValue = link.attr("rel");
                if (relValue.contains("icon")) {
                    faviconUrl = link.absUrl("href");
                    break;
                }
            }

            return new String[]{faviconUrl, title, url};
        } catch (IOException e) {
            System.err.println("Error fetching the webpage: " + e.getMessage());
            return new String[]{"Error retrieving title", "Error retrieving favicon"};
        }
    }

    /**
     * Extracts the title and icon from the given executable file location
     * @param exeLocation
     *      The location of the executable file
     * @return
     *      An array containing the icon URL, title, and location
     */
    public static String[] getExecutableTitleAndIcon(String exeLocation)
    {
        String regex = "([^\\\\]+)";
        ArrayList<String> matches = new ArrayList<>();

        // Compile the regex
        Pattern pattern = Pattern.compile(regex);

        System.out.println("Matches in path: " + exeLocation);
        Matcher matcher = pattern.matcher(exeLocation);
        int count = 1;
        while (matcher.find())
        {
            System.out.println("Segment " + count + ": " + matcher.group(1));
            matches.add(matcher.group(1));
            count++;
        }

        System.out.println("This is the Application: " + matches.get(matches.size()-2));
        String foundTitle = matches.get(matches.size()-2);

        String fileIcon = "https://winaero.com/blog/wp-content/uploads/2018/12/file-explorer-folder-libraries-icon-18298.png";

        return new String[]{fileIcon, foundTitle, exeLocation};
    }

    /**
     * Handles the application file search
     * @return
     *      The location of the application selected otherwise returns "File Not Found"
     */
    public String fileSearch() {
        JFrame frame = new JFrame("Open File");

        // Create a file chooser.
        JFileChooser fileChooser = new JFileChooser();

        // Set the file chooser to open at the current directory.
        fileChooser.setCurrentDirectory(new File("."));

        // Open the file chooser dialog.
        int result = fileChooser.showOpenDialog(frame);

        // If the user selects a file.
        if (result == JFileChooser.APPROVE_OPTION) {
            // Get the selected file.
            File selectedFile = fileChooser.getSelectedFile();
            // Display the path of the selected file.
            System.out.println("Selected file: " + selectedFile.getAbsolutePath());
            addAppButton.setDisable(false);
            return selectedFile.getAbsolutePath();
        } else{
            addAppButton.setDisable(true);
            return "File Not Found";
        }
    }

    /**
     * Opens confirmation pane is changes are not saved. Otherwise
     * it loads the main-view FXML and the appropriate stylesheet
     * @throws IOException
     *      If an exception occurred while loading the FXML file
     */
    @FXML
    protected void onCancelButtonClick() throws IOException {
        if(!changesSaved){
            blackOutStackPane.setVisible(true);
            confirmCancelStackPane.setVisible(true);
        } else {
            Stage stage = (Stage) cancelButton.getScene().getWindow();
            FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("fxml/main-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), HelloApplication.WIDTH, HelloApplication.HEIGHT);

            if (UserConfig.FindCSSFile()){
                scene.getStylesheets().add(UserConfig.getCSSFilePath().toUri().toString());
            } else {
                scene.getStylesheets().add(Objects.requireNonNull(HelloApplication.class.getResource("stylesheet.css")).toExternalForm());
            }
            stage.setScene(scene);
        }

    }

    /**
     * Syncs the blocked applications data with the table view by
     * retrieving the blocked items and assigning it to table columns
     */
    public void syncBlockedApplications()
    {
//        ObservableList<BlockedApplication> dataList = blockedDAO.dataList(blockedDAO.getAllApplications());

        ObservableList<BlockedItem> dataList = FXCollections.observableArrayList(blockedItems);

        tableView.setItems(dataList);


        iconColumn.setCellValueFactory(new PropertyValueFactory<>("iconURI"));

        iconColumn.setCellFactory(tc -> new TableCell<BlockedItem, String>() {
            private final ImageView imageView = new ImageView();

            @Override
            protected void updateItem(String url, boolean empty) {
                super.updateItem(url, empty);
                if (empty || url == null) {
                    setGraphic(null);
                    setText(null);
                } else {
                    try {
                        // Download the .ico file
                        File icoFile = File.createTempFile("favicon", ".ico");
                        FileUtils.copyURLToFile(new URL(url), icoFile);

                        // Convert the .ico file to a BufferedImage
                        BufferedImage bufferedImage = Imaging.getBufferedImage(icoFile);

                        // Create a temporary file for the converted image
                        File tempFile = File.createTempFile("temp_image", ".png");
                        tempFile.deleteOnExit();
                        ImageIO.write(bufferedImage, "png", tempFile);

                        // Load the image using the temporary file path
                        Image fxImage = new Image(tempFile.toURI().toString(), true);  // true for background loading
                        imageView.setImage(fxImage);
                        imageView.setFitHeight(30); // Adjust size as needed
                        imageView.setFitWidth(30);
                        imageView.setPreserveRatio(true);
                        setGraphic(imageView);
                    } catch (IOException | ImageReadException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    /**
     * Initialise the controller automatically after the FXML file is loaded
     * @param url
     *      Used to resolve paths for objects. Returns null if path is unknown
     * @param resourceBundle
     *      Used to localise an object. Returns null if it can not localise.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        iconColumn.setCellValueFactory(new PropertyValueFactory<BlockedItem, String>("iconURI"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<BlockedItem, String>("name"));
        locationColumn.setCellValueFactory(new PropertyValueFactory<BlockedItem, String>("URI"));

        syncBlockedApplications();

        // Automatically set as true until change is made
        changesSaved = true;

    }



    public static String removeHttp(String url) {
        if (url.startsWith("https://")) {
            return url.substring(8);
        }
        return url;
    }

    public static void blockCurrentUrls() {
        List<String> urlsToBlock = new ArrayList<>();

        for (BlockedItem item : blockedItems) {
            if (item.getClass() == WebsiteItem.class) {
                System.out.println(item.getName() + " is website");
                urlsToBlock.add(item.getURI());
            }

        }

        try{
            blockUrls(urlsToBlock);
        } catch (IOException e){
            System.out.println(e);
        }

    }

    public static void blockUrls(List<String> urls) throws IOException {
        // Note that this code only works in Java 7+,
        // refer to the above link about appending files for more info

        //resetBlockedList();
        List<String> newURLs = new ArrayList<>();

        String headerContent =  "# Copyright (c) 1993-2009 Microsoft Corp.\n" +
                "#\n" +
                "# This is a sample HOSTS file used by Microsoft TCP/IP for Windows.\n" +
                "#\n" +
                "# This file contains the mappings of IP addresses to host names. Each\n" +
                "# entry should be kept on an individual line. The IP address should\n" +
                "# be placed in the first column followed by the corresponding host name.\n" +
                "# The IP address and the host name should be separated by at least one\n" +
                "# space.\n" +
                "#\n" +
                "# Additionally, comments (such as these) may be inserted on individual\n" +
                "# lines or following the machine name denoted by a '#' symbol.\n" +
                "#\n" +
                "# For example:\n" +
                "#\n" +
                "#      102.54.94.97     rhino.acme.com          # source server\n" +
                "#       38.25.63.10     x.acme.com              # x client host\n" +
                "\n" +
                "# localhost name resolution is handled within DNS itself.\n" +
                "#\t127.0.0.1       localhost\n" +
                "#\t::1             localhost\n";

        newURLs.add(headerContent);

        for (String url : urls) {
            newURLs.add("127.0.0.1 " + removeHttp(url) + "\n");
        }


        // Get OS name
        String OS = System.getProperty("os.name").toLowerCase();

        // Use OS name to find correct location of hosts file
        String hostsFile = "";
        if ((OS.indexOf("win") >= 0)) {
            // Doesn't work before Windows 2000
            hostsFile = "C:\\Windows\\System32\\drivers\\etc\\hosts";
        } else if ((OS.indexOf("mac") >= 0)) {
            // Doesn't work before OS X 10.2
            hostsFile = "etc/hosts";
        } else if ((OS.indexOf("nux") >= 0)) {
            hostsFile = "/etc/hosts";
        } else {
            // Handle error when platform is not Windows, Mac, or Linux
            System.err.println("Sorry, but your OS doesn't support blocking.");
            System.exit(0);
        }

        System.out.println("Block Websites: " + urls);
        // Actually block site
//        Files.write(Paths.get(hostsFile),
//                ("127.0.0.1 " + url + "\n").getBytes(),
//                StandardOpenOption.APPEND);}
        //Files.write(Paths.get(hostsFile), headerContent.getBytes(), StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);
        //Files.write(Paths.get(hostsFile), newURLs, StandardOpenOption.APPEND);
        Files.write(Paths.get(hostsFile), newURLs, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);
    }


    public static void resetBlockedList() {
        // Note that this code only works in Java 7+,
        // refer to the above link about appending files for more info

        // Get OS name
        String OS = System.getProperty("os.name").toLowerCase();

        // Use OS name to find correct location of hosts file
        String hostsFile = "";
        if ((OS.indexOf("win") >= 0)) {
            // Doesn't work before Windows 2000
            hostsFile = "C:\\Windows\\System32\\drivers\\etc\\hosts";
        } else if ((OS.indexOf("mac") >= 0)) {
            // Doesn't work before OS X 10.2
            hostsFile = "etc/hosts";
        } else if ((OS.indexOf("nux") >= 0)) {
            hostsFile = "/etc/hosts";
        } else {
            // Handle error when platform is not Windows, Mac, or Linux
            System.err.println("Sorry, but your OS doesn't support blocking.");
            System.exit(0);
        }

        // Actually block site
        try{
            Files.write(Paths.get(hostsFile),
                    ("# Copyright (c) 1993-2009 Microsoft Corp.\n" +
                            "#\n" +
                            "# This is a sample HOSTS file used by Microsoft TCP/IP for Windows.\n" +
                            "#\n" +
                            "# This file contains the mappings of IP addresses to host names. Each\n" +
                            "# entry should be kept on an individual line. The IP address should\n" +
                            "# be placed in the first column followed by the corresponding host name.\n" +
                            "# The IP address and the host name should be separated by at least one\n" +
                            "# space.\n" +
                            "#\n" +
                            "# Additionally, comments (such as these) may be inserted on individual\n" +
                            "# lines or following the machine name denoted by a '#' symbol.\n" +
                            "#\n" +
                            "# For example:\n" +
                            "#\n" +
                            "#      102.54.94.97     rhino.acme.com          # source server\n" +
                            "#       38.25.63.10     x.acme.com              # x client host\n" +
                            "\n" +
                            "# localhost name resolution is handled within DNS itself.\n" +
                            "#\t127.0.0.1       localhost\n" +
                            "#\t::1             localhost\n").getBytes(),
                    StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);
        } catch (IOException e){
            System.out.println(e);
        }
    }

    /**
     * Saves new blocked applications and websites to the database
     * And loads the main-view FXML and the appropriate stylesheet
     * @param actionEvent
     *      Triggered when pressed
     * @throws IOException
     *      If an exception occurred while loading the FXML file
     */
    public void onSaveButtonClick(ActionEvent actionEvent) throws IOException {

        for (BlockedItem item : blockedItems) {
            if (item.getClass() == WebsiteItem.class) {
                System.out.println(item.getName() + " is website");
                presetDAO.addWebsitePreset(currentPreset.getPresetID(), item.getID());
            } else if (item.getClass() == ApplicationItem.class) {
                System.out.println(item.getName() + " is application");
                presetDAO.addApplicationPreset(currentPreset.getPresetID(), item.getID());
            }

        }

        changesSaved = true;
        Stage stage = (Stage) saveButton.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("fxml/main-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), HelloApplication.WIDTH, HelloApplication.HEIGHT);

        if (UserConfig.FindCSSFile()){
            scene.getStylesheets().add(UserConfig.getCSSFilePath().toUri().toString());
        } else {
            scene.getStylesheets().add(Objects.requireNonNull(HelloApplication.class.getResource("stylesheet.css")).toExternalForm());
        }
        stage.setScene(scene);
    }

    /**
     * Enables visibility of the blackout and add website pane when clicked
     * @param actionEvent
     *      Triggered when pressed
     */
//    Navigate to form page or, open stack pane
    public void onWebsiteButtonClick(ActionEvent actionEvent) {
        blackOutStackPane.setVisible(true);
        addWebsiteStackPane.setVisible(true);

    }

    /**
     * Enables visibility of the blackout and add application pane when clicked
     * @param actionEvent
     *      Triggered when pressed
     */
    public void onApplicationButtonClick(ActionEvent actionEvent) {
        blackOutStackPane.setVisible(true);
        addApplicationStackPane.setVisible(true);
    }

    /**
     * Disables visibility of the blackout,
     * add website and add app pane when clicked
     * @param mouseEvent
     *      Triggered when pressed
     */
    public void onXLabelClick(ActionEvent mouseEvent) {
        blackOutStackPane.setVisible(false);
        addWebsiteStackPane.setVisible(false);
        addApplicationStackPane.setVisible(false);
    }

    /**
     * Stores to temporary database things, wait for save button to be clicked
     * @param actionEvent
     *      Triggered when pressed
     */
    @FXML
    public void onAddButtonClick(ActionEvent actionEvent) {
//        Get addWebsiteTextField contents
        if(addWebsiteStackPane.isVisible())
        {
            addNewWebsite("https://" + addWebsiteTextField.getText());
            addWebsiteTextField.clear();
        }
        else
        {
            addNewApplication(fileLocation);
        }

        changesSaved = false;
        blackOutStackPane.setVisible(false);
        addWebsiteStackPane.setVisible(false);
        addApplicationStackPane.setVisible(false);
    }

    /**
     * Handles the status of the block button based on if something is
     * entered in the text field or not
     * @param keyEvent
     *      Triggered when something is entered in the text field
     */
    public void websiteEntered(KeyEvent keyEvent) {
        addWebButton.setDisable(false);
    }

    /**
     * Calls the fileSearch function when triggered
     */
    public void onFileClick() {
        fileLocation = fileSearch();
        addApplicationTextField.setText(fileLocation);

    }


    @FXML
    private Button confirmButton;

    /**
     * Handles the onConfirmButtonClick action by loading the
     * main-view FXML and the appropriate stylesheet
     * @throws IOException
     *      If an exception occurred while loading the FXML file
     */
    public void onConfirmButtonClick() throws IOException {
        Stage stage = (Stage) confirmButton.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("fxml/main-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), HelloApplication.WIDTH, HelloApplication.HEIGHT);

        scene.getStylesheets().add(Objects.requireNonNull(HelloApplication.class.getResource("stylesheet.css")).toExternalForm());
        stage.setScene(scene);
    }

    /**
     * Hides the cancel pane and block out pane
     */
    public void onAbortButtonClick() {
        blackOutStackPane.setVisible(false);
        confirmCancelStackPane.setVisible(false);
    }
}