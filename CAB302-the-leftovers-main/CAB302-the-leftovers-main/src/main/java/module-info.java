module focusApp {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.desktop;
    requires jdk.dynalink;
    requires jdk.jshell;
    requires org.xerial.sqlitejdbc;
    requires freetts;
    requires org.jsoup;
    requires org.controlsfx.controls;
    requires org.apache.commons.imaging;
    requires commons.io;
    requires fr.brouillard.oss.cssfx;


    opens focusApp to javafx.fxml;
    exports focusApp;
    exports focusApp.database;
    exports focusApp.controllers;
    opens focusApp.controllers to javafx.fxml;
    exports focusApp.models.colour;
    opens focusApp.models.colour to javafx.fxml;
    exports focusApp.models.timer;
    opens focusApp.models.timer to javafx.fxml;
    exports focusApp.models.user;
    opens focusApp.models.user to javafx.fxml;
    exports focusApp.models.block;
    opens focusApp.models.block to javafx.fxml;
    exports focusApp.models.preset;
    opens focusApp.models.preset to javafx.fxml;
}