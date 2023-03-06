/*
 * Trivia by MarCarrot, 2020
 */

package me.marcarrots.triviatreasure.data;

import me.marcarrots.triviatreasure.TriviaTreasure;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Level;

public class FileManager {

    private final TriviaTreasure triviaTreasure;
    private final String fileName;
    private FileConfiguration fileConfiguration;
    private File dataFile;

    public FileManager(TriviaTreasure triviaTreasure, String fileName) {
        this.triviaTreasure = triviaTreasure;
        this.fileConfiguration = null;
        this.dataFile = null;
        this.fileName = fileName;
        saveDefaultConfig();
    }

    public void reloadFiles() {
        createFile();
        fileConfiguration = YamlConfiguration.loadConfiguration(dataFile);
        InputStream defaultStream = triviaTreasure.getResource(fileName);
        if (defaultStream != null) {
            YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(defaultStream));
            fileConfiguration.setDefaults(defaultConfig);
        }
    }

    public FileConfiguration getData() {
        if (fileConfiguration == null) {
            reloadFiles();
        }
        return fileConfiguration;
    }

    public void saveData() {
        if (fileConfiguration == null || dataFile == null) {
            return;
        }
        try {
            getData().save(dataFile);
        } catch (IOException e) {
            triviaTreasure.getLogger().log(Level.SEVERE, "Could not save " + fileName);
            e.printStackTrace();
        }
    }

    public void saveDefaultConfig() {
        createFile();
        if (!dataFile.exists()) {
            triviaTreasure.saveResource(fileName, false);
        }
    }

    private void createFile() {
        if (dataFile == null) {
            dataFile = new File(triviaTreasure.getDataFolder(), fileName);
        }
    }

}
