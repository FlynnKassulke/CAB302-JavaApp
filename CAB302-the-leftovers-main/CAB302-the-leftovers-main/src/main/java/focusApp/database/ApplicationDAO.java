package focusApp.database;

import focusApp.models.block.ApplicationItem;
import org.sqlite.SQLiteErrorCode;
import org.sqlite.SQLiteException;

import java.sql.*;

public class ApplicationDAO implements IApplicationDAO {
    private Connection connection;

    public ApplicationDAO() {
        this.connection = DatabaseConnection.getInstance();
        Tables.createTables();
    }

    @Override
    public ApplicationItem addApplication(String name, String fileLocation) {
        try {

            String query = "INSERT INTO applications(applicationName, url) VALUES(?,?)";
            PreparedStatement statement = connection.prepareStatement(query);

            statement.setString(1, name);
            statement.setString(2, fileLocation);

            statement.execute();


            int id = getApplicationID(name);

            return new ApplicationItem(id, name, fileLocation, null);

        } catch (SQLiteException sqlex) {
            /* 19 is unique constraint error code */
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
    public ApplicationItem addApplication(String name, String fileLocation, String iconLocation) {
        try {
            if (getApplicationID(name) != -1) {
                return null;
            }

            String query = "INSERT INTO applications(applicationName, url, icon) VALUES(?,?,?)";
            PreparedStatement statement = connection.prepareStatement(query);

            statement.setString(1, name);
            statement.setString(2, fileLocation);
            statement.setString(3, iconLocation);

            statement.execute();

            int id = getApplicationID(name);

            if (id != -1) {
                return new ApplicationItem(id, name, fileLocation, iconLocation);
            }

            return null;

        } catch (SQLException sqlex) {
            /* 19 is unique constrain error */
            if (sqlex.getErrorCode() == 19) {
                return null;
            }
            sqlex.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public boolean changeApplicationName(int id, String newName) {
        try {
            String query = "UPDATE applications SET applicationName = ? WHERE applicationID = ?";
            PreparedStatement statement = connection.prepareStatement(query);

            statement.setString(1, newName);
            statement.setInt(2, id);

            int res = statement.executeUpdate();

            if (res != 0) {
                return true;
            }

        } catch (SQLException sqlex) {
            /* 19 is unique constrain error code */
            if (sqlex.getErrorCode() == 19) {
                return false;
            }
            sqlex.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean changeApplicationUrl(int id, String newUrl) {
        try {
            String query = "UPDATE applications SET url = ? WHERE applicationID = ?";
            PreparedStatement statement = connection.prepareStatement(query);

            statement.setString(1, newUrl);
            statement.setInt(2, id);

            int res = statement.executeUpdate();

            if (res != 0) {
                return true;
            }

        } catch (SQLException sqlex) {
//            if (sqlex.getResultCode() == SQLiteErrorCode.SQLITE_CONSTRAINT_UNIQUE) {
//                if (sqlex.getResultCode() == SQLiteErrorCode.SQLITE_CONSTRAINT_UNIQUE) {
//                    return false;
//                }
                sqlex.printStackTrace();
//            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean changeApplicationIcon(int id, String newIconUrl) {
        try {
            String query = "UPDATE applications SET icon = ? WHERE applicationID = ?";
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

    public int getApplicationID(String name) {
        try {
            String query = "SELECT applicationID FROM applications WHERE applicationName = ?";
            PreparedStatement  statement = connection.prepareStatement(query);

            statement.setString(1, name);

            ResultSet res = statement.executeQuery();

            if (res != null) {
                return res.getInt("applicationID");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return -1;
    }

    @Override
    public ApplicationItem getApplication(int id) {
        try {
            String query = "SELECT applicationID, applicationName, url, icon FROM applications WHERE applicationID = ?";
            PreparedStatement statemnt = connection.prepareStatement(query);

            statemnt.setInt(1, id);

            ResultSet res = statemnt.executeQuery();

            if (res != null) {
                return new ApplicationItem(res.getInt("applicationID"), res.getString("applicationName"), res.getString("url"), res.getString("icon"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
