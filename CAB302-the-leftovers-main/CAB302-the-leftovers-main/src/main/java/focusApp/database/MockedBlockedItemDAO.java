package focusApp.database;

import focusApp.models.block.BlockedApplication;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
public class MockedBlockedItemDAO implements IBlockedItemDAO{


    public static final ArrayList<BlockedApplication> blockedItems = new ArrayList<>();
    public static int autoIncrementedID = 0;

    private Connection connection;

    public MockedBlockedItemDAO()
    {
        this.connection = DatabaseConnection.getInstance();
        Tables.createTables();
    }

    @Override
    public void addApplication(BlockedApplication blockedApplication) {
        blockedApplication.setId(autoIncrementedID);
        autoIncrementedID++;
        blockedItems.add(blockedApplication);
    }

    @Override
    public void updateContact(BlockedApplication blockedApplication) {
        for (int i = 0; i < blockedItems.size(); i++) {
            if (blockedItems.get(i).getId() == blockedApplication.getId()) {
                blockedItems.set(i, blockedApplication);
                break;
            }
        }
    }

    @Override
    public void deleteContact(String blockedName) {

//        blockedItems.remove(blockedItem);
        try {
            String query = "SELECT websiteID FROM websites WHERE websiteName = ?";
            PreparedStatement statement = connection.prepareStatement(query);

            statement.setString(1, String.valueOf(blockedName));

            ResultSet res = statement.executeQuery();

            if (res == null) {
                return;
            }

            int websiteID = res.getInt("websiteID");

            /* delete dependencies */
            query = "DELETE FROM websites WHERE websiteID = ?";
            statement = connection.prepareStatement(query);
            statement.setInt(1, websiteID);
            statement.executeUpdate();


            if (statement.executeUpdate() != 0) {
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public BlockedApplication getBlockedApplication(int id) {
        for (BlockedApplication blockedApplication : blockedItems) {
            if (blockedApplication.getId() == id) {
                return blockedApplication;
            }
        }
        return null;
    }

    @Override
    public List<BlockedApplication> getAllApplications() {
        return new ArrayList<>(blockedItems);
    }

    @Override
    public ObservableList<BlockedApplication> dataList(List<BlockedApplication> applications) {
        return FXCollections.observableArrayList(applications);
    }
}
