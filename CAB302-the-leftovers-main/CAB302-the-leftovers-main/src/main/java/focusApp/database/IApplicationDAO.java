package focusApp.database;

import focusApp.models.block.ApplicationItem;

public interface IApplicationDAO {

    /**
     * adds an application to the database
     * @param name application name
     * @param fileLocation application uri location
     */
    public ApplicationItem addApplication(String name, String fileLocation);

    /**
     * adds an application to the database
     * @param name application name
     * @param fileLocation application location
     * @param iconLocation application icon location
     */
    public ApplicationItem addApplication(String name, String fileLocation, String iconLocation);

    /**
     * changes the application name
     * @param id application id
     * @param newName name to replace the current name
     * @return true if change made false otherwise
     */
    public boolean changeApplicationName(int id, String newName);

    /**
     * change the url for the application
     * @param id application id
     * @param newUrl url to replace the current url
     * @return true if change made false otherwise
     */
    public boolean changeApplicationUrl(int id, String newUrl);

    /**
     * changes the application icon url
     * @param id the id of the application to be changed
     * @param newIconUrl the url to the new icon
     * @return true if change made false otherwise
     */
    public boolean changeApplicationIcon(int id, String newIconUrl);

    /**
     * returns the id of an application based on its name or -1 if not found
     * @param name the name of the application
     * @return the id of the application or -1 if not found
     */
    public int getApplicationID(String name);

    /**
     * gets a website base on an id
     * @param id the id of the website
     * @return website class
     */
    public ApplicationItem getApplication(int id);
}
