package focusApp.database;

import focusApp.models.block.WebsiteItem;
import org.sqlite.SQLiteErrorCode;
import org.sqlite.SQLiteException;

import java.sql.*;

public class WebsiteDAO implements IWebsiteDAO {
    private Connection connection;

    public WebsiteDAO() {
        this.connection = DatabaseConnection.getInstance();
        Tables.createTables();
    }

    @Override
    public WebsiteItem addWebsite(String name, String url) {
        try {
            String query = "INSERT INTO websites(websiteName, url)  VALUES(?,?)";
            PreparedStatement statement = connection.prepareStatement(query);

            statement.setString(1, name);
            statement.setString(2, url);

            statement.execute();

            int id = getWebsiteID(name);

            return new WebsiteItem(id, name, url, null);

        } catch (SQLiteException sqlex) {
            if (sqlex.getResultCode() == SQLiteErrorCode.SQLITE_CONSTRAINT_UNIQUE) {
               return null;
            }
            sqlex.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public WebsiteItem addWebsite(String name, String url, String iconUri) {
        try {
            String query = "INSERT INTO websites(websiteName, url, icon) VALUES(?,?,?)";
            PreparedStatement statement = connection.prepareStatement(query);

            statement.setString(1, name);
            statement.setString(2 ,url);
            statement.setString(3, iconUri);

            statement.execute();

            int id = getWebsiteID(name);

            return new WebsiteItem(id, name, url, iconUri);

        } catch (SQLiteException sqlex) {
            if (sqlex.getResultCode() == SQLiteErrorCode.SQLITE_CONSTRAINT_UNIQUE) {
                return null;
            }
            sqlex.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean changeWebsiteName(int id, String newName) {
        try {
            String query = "UPDATE websites SET websiteName = ? WHERE websiteID = ?";
            PreparedStatement statement = connection.prepareStatement(query);

            statement.setString(1, newName);
            statement.setInt(2, id);

            int res = statement.executeUpdate();

            if (res != 0) {
                return true;
            }

        } catch (SQLiteException sqlex) {
            if (sqlex.getResultCode() == SQLiteErrorCode.SQLITE_CONSTRAINT_UNIQUE) {
                return false;
            }
            sqlex.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean changeWebsiteUrl(int id, String newUrl) {
        try {
            String query = "UPDATE websites SET url = ? WHERE websiteID = ?";
            PreparedStatement statement = connection.prepareStatement(query);

            statement.setString(1, newUrl);
            statement.setInt(2, id);

            int res = statement.executeUpdate();

            if (res != 0) {
                return true;
            }

        } catch (SQLiteException sqlex) {
            if (sqlex.getResultCode() == SQLiteErrorCode.SQLITE_CONSTRAINT_UNIQUE) {
                return false;
            }
            sqlex.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean changeWebsiteIcon(int id, String newIconUrl) {
        try {
            String query = "UPDATE websites SET icon = ? WHERE websiteID = ?";
            PreparedStatement statement = connection.prepareStatement(query);

            statement.setString(1, newIconUrl);
            statement.setInt(2, id);

            int res = statement.executeUpdate();

            if (res != 0) {
                return true;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public int getWebsiteID(String name) {
        try {
            String query = "SELECT websiteID FROM websites WHERE websiteName = ?";
            PreparedStatement statement = connection.prepareStatement(query);

            statement.setString(1, name);

            ResultSet res = statement.executeQuery();

            if (res.next()) {
                return res.getInt("websiteID");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    @Override
    public WebsiteItem getWebsite(int id) {
        try {
            String query = "SELECT websiteName, url, icon FROM websites WHERE websiteID = ?";
            PreparedStatement statement = connection.prepareStatement(query);

            statement.setInt(1, id);

            ResultSet res = statement.executeQuery();

            if (res.next()) {
                return new WebsiteItem(id, res.getString("websiteName"), res.getString("url"), res.getString("icon"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
