package focusApp.models.colour;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class UserConfig {
    /**
     * The Configuration path for all Use Files.
     */
    private static final Path ConfigPath = Paths.get(System.getProperty("user.home"), "/HyperFocus/");

    /**
     * The path to the CSS file.
     */
    private static final Path CSSFile = Paths.get(String.valueOf(ConfigPath), "stylesheets.css");

    /**
     * Get the path to the CSS file.
     * @return The path to the CSS file
     */
    public static Path getCSSFilePath(){ return CSSFile; }

    /**
     * Used for configuring the user directory.
     */
    private enum Config{
        Directory, CSSFile
    }

    /**
     * Used to check if the user environment is set up correctly.
     */
    public static void SetUpEnvironment(){

        boolean Directory = Files.isDirectory(ConfigPath);
        boolean File = Files.exists(CSSFile);

        if (!Directory){
            CreateUserConfig(Config.Directory);
            CreateUserConfig(Config.CSSFile);
        } else if (!File){
            CreateUserConfig(Config.CSSFile);
        }
    }

    /**
     * Creates the user environment.
     * @param ConfigType This defines what is needed when creating the environment.
     */
    private static void CreateUserConfig(Config ConfigType){
        try{
            switch (ConfigType){
                case Directory -> Files.createDirectories(ConfigPath);
                case CSSFile -> SetUpCSSFile();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Sets up the users CSS file and injects the base palette.
     * @throws IOException If the file can't be created.
     */
    private static void SetUpCSSFile() throws IOException {
        Files.createFile(CSSFile);
        ColourControl.InjectBasePalette();
    }

    /**
     * Checks if the Users CSS file exists.
     * @return true if the file exists, false if it doesn't exist.
     */
    public static boolean FindCSSFile(){
        return Files.exists(CSSFile);
    }
}
