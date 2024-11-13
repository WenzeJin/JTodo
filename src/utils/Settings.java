package utils;

import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Manages application settings, including task storage paths, in a singleton.
 * Loads settings from a JSON file or creates it if it does not exist.
 */
public class Settings {

    private static final String SETTINGS_FILE = "settings.json";
    private static Settings instance;

    // Default task save path
    private String taskSavePath = "./tasks-saving.data";

    // Auto-saving
    private boolean autoSave = true;

    // Private constructor to enforce singleton pattern
    private Settings() {
        loadSettings();  // Automatically load settings on instantiation
    }

    /**
     * Gets the single instance of Settings.
     *
     * @return the instance of Settings
     */
    public static Settings getInstance() {
        if (instance == null) {
            instance = new Settings();
        }
        return instance;
    }

    /**
     * Loads settings from the JSON file. If the file does not exist, saves the default settings.
     */
    private void loadSettings() {
        File file = new File(SETTINGS_FILE);
        if (file.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
                StringBuilder content = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    content.append(line);
                }
                JSONObject json = new JSONObject(content.toString());
                this.taskSavePath = json.optString("taskSavePath", "./tasks-saving.data");
                this.autoSave = json.optBoolean("autoSave", true);
            } catch (IOException e) {
                System.err.println("Error loading settings file; using default settings.");
                e.printStackTrace();
            }
        } else {
            saveSettings();  // Create settings file if it does not exist
        }
    }

    /**
     * Saves the current settings to the JSON file.
     */
    public void saveSettings() {
        JSONObject json = new JSONObject();
        json.put("taskSavePath", this.taskSavePath);
        json.put("autoSave", this.autoSave);

        try (FileWriter writer = new FileWriter(SETTINGS_FILE, StandardCharsets.UTF_8)) {
            writer.write(json.toString(4));  // Write formatted JSON output
        } catch (IOException e) {
            System.err.println("Error saving settings file.");
            e.printStackTrace();
        }
    }

    /**
     * Gets the task save path.
     *
     * @return the task save path
     */
    public String getTaskSavePath() {
        return taskSavePath;
    }

    /**
     * Gets the setting of auto-saving
     *
     * @return boolean value of setting
     */
    public boolean getAutoSaveSetting() {
        return autoSave;
    }

    /**
     * Sets the task save path and saves settings.
     *
     * @param path the new task save path
     */
    public void setTaskSavePath(String path) {
        this.taskSavePath = path;
        saveSettings();
    }


}