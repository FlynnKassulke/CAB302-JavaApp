package focusApp.database;

import focusApp.models.block.ApplicationItem;
import focusApp.models.block.WebsiteItem;
import org.sqlite.SQLiteErrorCode;
import org.sqlite.SQLiteException;

import java.awt.desktop.SystemSleepEvent;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.sql.Connection;

public class PresetDAO implements IPresetDAO {
    private Connection connection;

    public PresetDAO() {
        this.connection = DatabaseConnection.getInstance();
        Tables.createTables();
    }

    @Override
    public ArrayList<Preset> getUsersPresets(int userID) {
        try {
            String query = "SELECT presetID, presetName FROM presets WHERE userID = ?";

            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, userID);

            ResultSet result = statement.executeQuery();

            ArrayList<Preset> output = new ArrayList<>();

            while (result.next()) {
                Preset presets = new Preset(result.getInt("presetID"), result.getString("presetName"));

                output.add(presets);
            }

            return output;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Preset addPreset(int userID, String presetName) {
        try {
            // Check if the preset name already exists
            String query = "SELECT COUNT(*) FROM presets WHERE userID = ? AND presetName = ?";
            PreparedStatement checkStatement = connection.prepareStatement(query);
            checkStatement.setInt(1, userID);
            checkStatement.setString(2, presetName);
            ResultSet resultSet = checkStatement.executeQuery();

            if (resultSet.next()) {
                // If the preset name already exists, append an integer suffix until a unique name is found
                int suffix = 1;
                String originalName = presetName;
                while (resultSet.getInt(1) > 0) {
                    presetName = originalName + " " + suffix;
                    resultSet.close();

                    // Check again if the new name exists
                    checkStatement.setInt(1, userID);
                    checkStatement.setString(2, presetName);
                    resultSet = checkStatement.executeQuery();
                    suffix++;
                }
            }

            // Insert the preset with the unique name into the database
            query = "INSERT INTO presets(userID, presetName) VALUES (?, ?)";
            PreparedStatement insertStatement = connection.prepareStatement(query);
            insertStatement.setInt(1, userID);
            insertStatement.setString(2, presetName);
            insertStatement.execute();

            // Get the ID of the inserted preset
            int id = getLastPreset(userID);

            return new Preset(id, presetName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean editPresetName(int userID, String currentName, String newName) {
        try {
            String query = "UPDATE presets SET presetName = ? WHERE userID = ? AND presetName = ?";
            PreparedStatement statement = connection.prepareStatement(query);

            statement.setString(1, newName);
            statement.setInt(2, userID);
            statement.setString(3, currentName);

            if (statement.executeUpdate() == 1) {
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

    public boolean deletePresetByName(int userID, String presetName) {
        try {
            String query = "SELECT presetID FROM presets WHERE presetName = ? AND userID = ?";
            PreparedStatement statement = connection.prepareStatement(query);

            statement.setString(1, presetName);
            statement.setInt(2, userID);

            ResultSet res = statement.executeQuery();

            if (res == null) {
                return false;
            }

            int presetID = res.getInt("presetID");

            /* delete dependencies */
            query = "DELETE FROM presetsToWebsite WHERE presetID = ?";
            statement = connection.prepareStatement(query);
            statement.setInt(1, presetID);
            statement.executeUpdate();

            /* delete dependencies */
            query = "DELETE FROM presetsToApplication WHERE presetID = ?";
            statement = connection.prepareStatement(query);
            statement.setInt(1, presetID);
            statement.executeUpdate();

            /* delete preset */
            query = "DELETE FROM presets WHERE presetID = ?";
            statement = connection.prepareStatement(query);
            statement.setInt(1, presetID);

            if (statement.executeUpdate() != 0) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean addWebsitePreset(int presetID, int websiteID) {
        try {
            String query = "INSERT INTO presetsToWebsite(presetID, websiteID) VALUES(?,?)";
            PreparedStatement statement = connection.prepareStatement(query);

           statement.setInt(1, presetID);
           statement.setInt(2, websiteID);

           return statement.execute();

        } catch (SQLiteException sqlex) {
            if (sqlex.getResultCode() == SQLiteErrorCode.SQLITE_CONSTRAINT_PRIMARYKEY) {
                return false;
            }
            sqlex.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean addApplicationPreset(int presetID, int applicationID) {
        try {
            String query = "INSERT INTO presetsToApplication(presetID, applicationID) VALUES(?,?)";
            PreparedStatement statement = connection.prepareStatement(query);

            statement.setInt(1, presetID);
            statement.setInt(2, applicationID);

            return statement.execute();

        } catch (SQLiteException sqlex) {
            if (sqlex.getResultCode() == SQLiteErrorCode.SQLITE_CONSTRAINT_PRIMARYKEY) {
                return false;
            }
            sqlex.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public ArrayList<ApplicationItem> getPresetApplication(int presetID) {
        try {
            String query = """
                    SELECT
                        app.applicationID, applicationName, url, icon FROM applications as app
                    INNER JOIN
                        presetsToApplication as preset ON preset.applicationID = app.applicationID
                    WHERE presetID = ?
                    """;

            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, presetID);

            ResultSet result = statement.executeQuery();

            ArrayList<ApplicationItem> output = new ArrayList<>();

            while (result.next()) {
                ApplicationItem application = new ApplicationItem(result.getInt("applicationID"), result.getString("applicationName"), result.getString("url"), result.getString("icon"));

                output.add(application);
            }

            return output;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public ArrayList<WebsiteItem> getPresetWebsite(int presetID) {
        try {
            String query = """
                    SELECT
                        web.websiteID, websiteName, url, icon FROM websites as web
                    INNER JOIN
                        presetsToWebsite as preset ON preset.websiteID = web.websiteID
                    WHERE presetID = ?
                    """;

            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, presetID);

            ResultSet result = statement.executeQuery();

            ArrayList<WebsiteItem> output = new ArrayList<>();

            while (result.next()) {
                WebsiteItem website = new WebsiteItem(result.getInt("websiteID"), result.getString("websiteName"), result.getString("url"), result.getString("icon"));

                output.add(website);
            }

            return output;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public int getLastPreset(int userID) {
        try {
            String query = "SELECT presetID FROM presets WHERE userID = ? ORDER BY presetID desc";
            PreparedStatement statement = connection.prepareStatement(query);

            statement.setInt(1, userID);

            ResultSet res = statement.executeQuery();

            if (res.next()) {
                return res.getInt("presetID");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }
}
