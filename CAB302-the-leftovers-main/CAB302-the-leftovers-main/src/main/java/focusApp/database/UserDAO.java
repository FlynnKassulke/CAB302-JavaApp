package focusApp.database;

import focusApp.models.user.User;
import org.sqlite.SQLiteErrorCode;
import org.sqlite.SQLiteException;

import javax.swing.plaf.basic.BasicInternalFrameTitlePane;
import java.sql.*;

public class UserDAO implements IUserDAO {
    private Connection connection;

    public UserDAO() {
        this.connection = DatabaseConnection.getInstance();
        /* method to make all required tables */
        Tables.createTables();
    }

    @Override
    public User addUser(String userName, String password) {
        try {
            /* insert user into database */
            String query =
                    """
                    INSERT INTO user(userName, password) VALUES(?,?)
                    """;
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, userName);
            statement.setString(2, password);

            statement.executeUpdate();

            /* attempt to log user in and return user class */
            return login(userName, password);

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
    public boolean updateName(int id, String newName) {
        try {
            String query = "UPDATE user SET userName = ? WHERE id = ?";

            PreparedStatement statement = connection.prepareStatement(query);

            statement.setString(1, newName);
            statement.setInt(2, id);

            return (statement.executeUpdate() != 0);

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
    public long addToTotalTime(int id, long time) {
        try {
            String query = "UPDATE user SET focusTime = focusTime + ? WHERE id = ?";
            PreparedStatement statement = connection.prepareStatement(query);

            statement.setLong(1, time);
            statement.setInt(2, id);

            if (statement.executeUpdate() != 1) {
                return -1;
            }

            query = "SELECT focusTime FROM user WHERE id = ?";

            statement = connection.prepareStatement(query);

            statement.setInt(1, id);

            ResultSet res = statement.executeQuery();

            if (res != null) {
                return res.getLong("focusTime");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return -1;
    }

    @Override
    public long getTotalTime(int id) {
        try {
            String query = "SELECT focusTime FROM user WHERE id = ?";

            PreparedStatement statement = connection.prepareStatement(query);

            statement.setInt(1, id);

            ResultSet res = statement.executeQuery();

            if (res != null) {
                return res.getLong("focusTime");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }


    public int getUserId(String userName) {
        try {
            String query = "SELECT id FROM user WHERE userName = ?";

            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, userName);

            ResultSet result = statement.executeQuery();

            if (result != null) {
                return result.getInt("id");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    @Override
    public boolean changePassword(int id, String newPassword) {
        try {
            String query = "UPDATE user SET password = ? WHERE id = ?";

            PreparedStatement statement = connection.prepareStatement(query);

            statement.setString(1, newPassword);
            statement.setInt(2, id);

            if (statement.executeUpdate() == 1) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public User login(String userName, String password) {
        try {
            /* get user from database */

            String query = "SELECT id, focusTime FROM user WHERE userName = ? AND password = ?";

            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, userName);
            statement.setString(2, password);

            ResultSet result = statement.executeQuery();

            if (!result.isClosed()) {
                /* if results is not null then user provided correct password and useranme */
                /* so create and return User class */
                int id = result.getInt("id");
                int focusTime = result.getInt("focusTime");

                User user = new User(userName, focusTime);
                user.setId(id);

                return user;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
