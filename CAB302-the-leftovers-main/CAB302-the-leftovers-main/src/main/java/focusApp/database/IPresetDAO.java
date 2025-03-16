package focusApp.database;

import focusApp.models.block.ApplicationItem;
import focusApp.models.block.WebsiteItem;

import java.util.ArrayList;

public interface IPresetDAO {

    /**
     * function to return the presets associated with a specific user
     * @param userID the id of the user
     * @return a list of presets
     */
    public ArrayList<Preset> getUsersPresets(int userID);

    /**
     * create a new preset
     * @param userID user id
     * @param presetName preset name
     * @return the preset that was created
     */
    public Preset addPreset(int userID, String presetName);

    /**
     * add website to a preset
     * @param presetID preset id
     * @param websiteID website id
     * @return preset website was added to
     */
    public boolean addWebsitePreset(int presetID, int websiteID);

    /**
     * add application to presets
     * @param presetID id of the preset to be added
     * @param applicationID id of the application to be added
     * @return preset the application was added to
     */
    public boolean addApplicationPreset(int presetID, int applicationID);

    /**
     * function to return the applications from a specific preset
     * @param presetID id of the preset
     * @return list of applications from preset
     */
    public ArrayList<ApplicationItem> getPresetApplication(int presetID);

    /**
     * function to return the websites from a specific preset
     * @param presetID
     * @return list of websites from preset
     */
    public ArrayList<WebsiteItem> getPresetWebsite(int presetID);

    /**
     * changes the name of a preset
     * @param userID id of the user
     * @param currentName the current name
     * @param newName the name to be changed to
     * @return true if operation ran successfully, false otherwise
     */
    public boolean editPresetName(int userID, String currentName, String newName);

    /**
     * deletes a given preset
     * @param userID the id of the user
     * @param presetName the name of the preset
     * @return true if operation ran successfully, false otherwise
     */
    public boolean deletePresetByName(int userID, String presetName);
}
