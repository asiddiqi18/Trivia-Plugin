/*
 * Trivia by MarCarrot, 2020
 */

package me.marcarrots.trivia.data;

import me.marcarrots.trivia.Trivia;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Level;

public class QuestionFileManager {

    private final Trivia trivia;
    private final String fileName;
    private FileConfiguration fileConfiguration;
    private File dataFile;

    public QuestionFileManager(Trivia trivia, String fileName) {
        this.trivia = trivia;
        this.fileConfiguration = null;
        this.dataFile = null;
        this.fileName = fileName;
        saveDefaultConfig();
    }

    public void reloadFiles() {
        createFile();
        this.fileConfiguration = YamlConfiguration.loadConfiguration(this.dataFile);
        InputStream defaultStream = this.trivia.getResource(this.fileName);
        if (defaultStream != null) {
            YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(defaultStream));
            this.fileConfiguration.setDefaults(defaultConfig);
        }
    }

    public FileConfiguration getData() {
        if (this.fileConfiguration == null)
            reloadFiles();
        return this.fileConfiguration;
    }

    public void saveData() {
        if (this.fileConfiguration == null || this.dataFile == null)
            return;
        try {
            getData().save(this.dataFile);
        } catch (IOException e) {
            Bukkit.getLogger().log(Level.SEVERE, "Could not save messages.yml!");
            e.printStackTrace();
        }
    }

    public void saveDefaultConfig() {
        createFile();
        if (!this.dataFile.exists())
            this.trivia.saveResource(this.fileName, false);
    }

    private void createFile() {
        if (this.dataFile == null)
            this.dataFile = new File(this.trivia.getDataFolder(), this.fileName);
    }

}
