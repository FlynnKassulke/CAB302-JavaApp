package focusApp.models.preset;

import focusApp.database.Preset;

/**
 * singleton class used to hold the user class for usage between files
 */
public class PresetHolder {
    private static PresetHolder Instance;
    private Preset preset;

    private PresetHolder() {}

    public static PresetHolder getInstance() {
        if (Instance == null) {
            Instance = new PresetHolder();
       }
        return Instance;
    }

    public Preset getPreset() {
        return preset;
    }

    public void setPreset(Preset preset) {
        this.preset = preset;
    }
}