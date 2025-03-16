package focusApp.database;

import focusApp.models.block.WebsiteItem;

public interface IWebsiteDAO {

    /**
     * adds a website to the database
     * @param name website name
     * @param url website url
     * @return websiteItem or null if website already existed
     */
    public WebsiteItem addWebsite(String name, String url);

    /**
     * adds a website to the database
     * @param name website name
     * @param url website url
     * @param iconUri file path to icon
     * @return websiteItem or null if website already existed
     */
    public WebsiteItem addWebsite(String name, String url, String iconUri);

    /**
     * changes the websites name in the db
     * @param id the id of the website
     * @param newName the name ot be changed
     * @return true if name changed false if not
     */
    public boolean changeWebsiteName(int id, String newName);

    /**
     * changes the url of the website
     * @param id the id of the website to be changed
     * @param newUrl the url of the website to be changed
     * @return true if url changed false otherwise
     */
    public boolean changeWebsiteUrl(int id, String newUrl);

    /**
     * changes the icon of the website
     * @param id the id of the website
     * @param newIconUrl the location of the new icon
     * @return true if icon changed, false otherwise
     */
    public boolean changeWebsiteIcon(int id, String newIconUrl);

    /**
     * gets the id of the website from a given name
     * @param name the name of the website
     * @return id of the website or -1 if no website found
     */
    public int getWebsiteID(String name);

    /**
     * gets a website based on its id
     * @param id the websites id
     * @return website class
     */
    public WebsiteItem getWebsite(int id);
}
