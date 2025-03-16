package focusApp.models.block;

public class BlockedItem {
    private int ID;
    private String Name;
    private String URI;
    private String IconURI;

    public BlockedItem(int ID, String name, String URI, String iconURI) {
        this.ID = ID;
        this.Name = name;
        this.URI = URI;
        this.IconURI = iconURI;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getName() {
        return Name;
    }

    public void setName(String Name) {
        this.Name = Name;
    }

    public String getURI() {
        return URI;
    }

    public void setURI(String URI) {
        this.URI = URI;
    }

    public String getIconURI() {
        return IconURI;
    }

    public void setIconURI(String IconURI) {
        this.IconURI = IconURI;
    }
}
