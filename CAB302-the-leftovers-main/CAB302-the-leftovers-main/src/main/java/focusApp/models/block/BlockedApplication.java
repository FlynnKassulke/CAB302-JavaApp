package focusApp.models.block;

public class BlockedApplication {
    //private String iconColumn;

    private int id;
    private String iconColumn;
    private String nameColumn;
    private String locationColumn;


    public BlockedApplication(String iconColumn, String nameColumn, String locationColumn) {
        this.iconColumn = iconColumn;
        this.nameColumn = nameColumn;
        this.locationColumn = locationColumn;
    }

    public String getIconColumn() {
        return iconColumn;
    }

    public String getNameColumn() {
        return nameColumn;
    }

    public String getLocationColumn() {
        return locationColumn;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setIconColumn(String iconColumn) {
        this.iconColumn = iconColumn;
    }

    public void setNameColumn(String nameColumn) {
        this.nameColumn = nameColumn;
    }

    public void setLocationColumn(String locationColumn) {
        this.locationColumn = locationColumn;
    }
}
