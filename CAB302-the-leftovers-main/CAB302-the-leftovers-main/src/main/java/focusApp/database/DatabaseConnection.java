package focusApp.database;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Class for getting connection to database
 */
public class DatabaseConnection {
    private static Connection instance = null;

    /**
     * creates a connection to the database
     */
    public DatabaseConnection() {
        String url = "jdbc:sqlite:database.db";
        try {
            instance = DriverManager.getConnection(url);
        } catch (SQLException sqlEx) {
            sqlEx.printStackTrace();
        }
    }

    /**
     * Gets a connection to the database
     * @return Connection to the database
     */
    public static Connection getInstance() {
        if (instance == null) {
            new DatabaseConnection();
        }
        return instance;
    }
}
