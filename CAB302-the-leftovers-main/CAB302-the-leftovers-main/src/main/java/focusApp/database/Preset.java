package focusApp.database;

public class Preset {
    private int presetID;
    private String presetName;

    public Preset(int id, String name) {
        presetID = id;
        presetName = name;
    }

    public int getPresetID() {
        return presetID;
    }

    public void setPresetID(int presetID) {
        this.presetID = presetID;
    }

    public String getPresetName() {
        return presetName;
    }

    public void setPresetName(String presetName) {
        this.presetName = presetName;
    }
}
