/*
 * Trivia by MarCarrot, 2020
 */

package me.marcarrots.trivia;

import me.marcarrots.trivia.commands.CommandManager;
import me.marcarrots.trivia.utils.TriviaPlaceholder;
import me.marcarrots.trivia.data.FileManager;
import me.marcarrots.trivia.language.Lang;
import me.marcarrots.trivia.listeners.ChatEvent;
import me.marcarrots.trivia.listeners.EntityDamage;
import me.marcarrots.trivia.listeners.InventoryClick;
import me.marcarrots.trivia.listeners.PlayerJoin;
import me.marcarrots.trivia.menu.PlayerMenuUtility;
import me.marcarrots.trivia.utils.UpdateChecker;
import net.milkbowl.vault.economy.Economy;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public final class Trivia extends JavaPlugin {

    private static Economy econ = null;
    private final HashMap<Player, PlayerMenuUtility> playerMenuUtilityMap = new HashMap<>();
    private QuestionContainer questionContainer;
    private Rewards[] rewards;
    private Game game = null;
    private FileManager questionsFile;
    private FileManager messagesFile;
    private FileManager rewardsFile;
    private String updateNotice = null;
    private AutomatedGameManager automatedGameManager;
    private NamespacedKey namespacedQuestionKey;
    private PlayerDataContainer stats;

    public static Economy getEcon() {
        return econ;
    }

    public NamespacedKey getNamespacedQuestionKey() {
        return namespacedQuestionKey;
    }

    public FileManager getQuestionsFile() {
        return questionsFile;
    }

    public FileManager getRewardsFile() {
        return rewardsFile;
    }

    public Rewards[] getRewards() {
        return rewards;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public void clearGame() {
        game = null;
    }

    public AutomatedGameManager getAutomatedGameManager() {
        return automatedGameManager;
    }

    public String getUpdateNotice() {
        return updateNotice;
    }

    public PlayerMenuUtility getPlayerMenuUtility(Player player) {
        PlayerMenuUtility playerMenuUtility;
        if (playerMenuUtilityMap.containsKey(player)) {
            return playerMenuUtilityMap.get(player);
        } else {
            playerMenuUtility = new PlayerMenuUtility(getConfig());
            playerMenuUtilityMap.put(player, playerMenuUtility);
            return playerMenuUtility;
        }
    }

    public QuestionContainer getQuestionHolder() {
        return questionContainer;
    }

    @Override
    public void onEnable() {


        questionContainer = new QuestionContainer();

        // load stuff from files
        loadConfigFile();
        loadQuestions();
        loadMessages();
        loadRewards();

        // register listeners and commands
        getServer().getPluginManager().registerEvents(new InventoryClick(), this);
        getServer().getPluginManager().registerEvents(new ChatEvent(this), this);
        getServer().getPluginManager().registerEvents(new PlayerJoin(this), this);
        getServer().getPluginManager().registerEvents(new EntityDamage(), this);
        Objects.requireNonNull(getCommand("trivia")).setExecutor(new CommandManager(this));

        // bStats
        try {
            new Metrics(this, 7912);
            getLogger().info("bStats has been loaded.");
        } catch (NoClassDefFoundError e) {
            getLogger().warning("bStats failed to load.");
            e.printStackTrace();
        }

        // vault
        if (setupEconomy()) {
            getLogger().info("Optional dependency 'vault' has been loaded");
        } else {
            getLogger().info("No vault has been detected, disabling vault features...");
        }

        // placeholder API
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new TriviaPlaceholder(this).register();
            getLogger().info("Optional dependency 'PlaceholderAPI' has been loaded");
        } else {
            getLogger().info("No PlaceholderAPI has been detected, disabling placeholder features...");
        }

        namespacedQuestionKey = new NamespacedKey(this, "trivia_question_id");

        stats = new PlayerDataContainer(this);

        // check for updates
        new UpdateChecker(this, 80401).getVersion(newVersion -> {
            String currentVersion = getDescription().getVersion();
            if (Integer.parseInt(newVersion.substring(2)) > Integer.parseInt(currentVersion.substring(2))) {
                updateNotice = String.format("%s - There is a new version available for Trivia (new version: %s, current version: %s)! Get it at: %s.", ChatColor.AQUA + "[Trivia!]" + ChatColor.YELLOW, ChatColor.GREEN + newVersion + ChatColor.YELLOW, ChatColor.GREEN + currentVersion + ChatColor.YELLOW, ChatColor.WHITE + "https://www.spigotmc.org/resources/trivia-easy-to-setup-game-%C2%BB-custom-rewards-%C2%BB-in-game-gui-menus-more.80401/" + ChatColor.YELLOW);
                getLogger().info(ChatColor.stripColor(updateNotice));
            }
        });

        // update config for older versions
        configUpdater();
    }

    @Override
    public void onDisable() {
        if (game != null) {
            game.getGameBossBar().hideBossBar();
            game = null;
        }
    }

    public void loadConfigFile() {
        getConfig().options().copyDefaults(true);
        saveDefaultConfig();

        if (automatedGameManager != null) {
            automatedGameManager.cancel();
        }
        automatedGameManager = new AutomatedGameManager(this);
        automatedGameManager.automatedSchedule();

    }

    public void loadQuestions() {
        questionsFile = new FileManager(this, "questions.yml");
        questionContainer.readQuestions(questionsFile);
    }


    public void loadMessages() {
        if (messagesFile == null) {
            messagesFile = new FileManager(this, "messages.yml");
        }

        messagesFile.reloadFiles();

        Lang.setFile(messagesFile.getData());

        Lang[] langValues = Lang.values();

        // add new keys to file
        List<String> addedKeys = new ArrayList<>();
        for (Lang val : langValues) {
            if (!messagesFile.getData().getKeys(true).contains(val.getPath())) {
                addedKeys.add(val.getPath());
                messagesFile.getData().set(val.getPath(), val.getDefault());
            }
        }

        // remove unused keys from file
        List<String> removedKeys = new ArrayList<>();
        if (langValues.length < messagesFile.getData().getKeys(false).size()) {
            Set<String> valStrings = new HashSet<>();
            for (Lang val : langValues) {
                valStrings.add(val.getPath());
            }
            for (String key : messagesFile.getData().getKeys(false)) {
                if (!valStrings.contains(key)) {
                    removedKeys.add(key);
                    messagesFile.getData().set(key, null);
                }
            }
            messagesFile.saveData();
        }


        if (addedKeys.size() > 0) {
            getLogger().info("[Trivia] The following keys were added to messages.yml: " + addedKeys);
            messagesFile.saveData();
        }
        if (removedKeys.size() > 0) {
            getLogger().info("[Trivia] The following keys were removed from messages.yml: " + removedKeys);
            messagesFile.saveData();
        }

    }


    public void loadRewards() {
        if (rewardsFile == null) {
            rewardsFile = new FileManager(this, "rewards.yml");
        }
        int rewardAmt = 4;
        rewardsFile.reloadFiles();
        rewards = new Rewards[rewardAmt];
        for (int i = 0; i < rewardAmt; i++) {
            rewards[i] = new Rewards(this, i);
        }
    }


    private boolean setupEconomy() {
        if (!isVaultEnabled()) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return true;
    }

    public boolean isVaultEnabled() {
        return getServer().getPluginManager().getPlugin("Vault") != null;
    }

    public void removeOldSoundKeys(HashMap<String, Object> newConfigKeys, String path, String defaultSound, double defaultPitch) {
        newConfigKeys.put(path + ".sound", getConfig().getString(path + " sound", defaultSound));
        getConfig().set(path + " sound", null);
        newConfigKeys.put(path + ".pitch", getConfig().getDouble(path + " pitch", defaultPitch));
        getConfig().set(path + " pitch", null);
        newConfigKeys.put(path + ".volume", 0.6d);
        getLogger().info("Removed old keys for " + path);
    }

    // transform old config to new config
    private void configUpdater() {
        HashMap<String, Object> newConfigKeys = new HashMap<>();
        HashMap<String, Object> newLangKeys = new HashMap<>();
        int currentConfigVersion = getConfig().getInt("Config Version");

        if (currentConfigVersion == 5) {

            removeOldSoundKeys(newConfigKeys, "Answer correct", "BLOCK_NOTE_BLOCK_PLING", 1.5);
            removeOldSoundKeys(newConfigKeys, "Time up", "ENTITY_VILLAGER_NO", 0.9);
            removeOldSoundKeys(newConfigKeys, "Game start", "ENTITY_PLAYER_LEVELUP", 1.0);
            removeOldSoundKeys(newConfigKeys, "Game over", "ENTITY_PLAYER_LEVELUP", 0.9);

            newConfigKeys.put("Question skipped.sound", "BLOCK_NOTE_BLOCK_PLING");
            newConfigKeys.put("Question skipped.pitch", 1.5d);
            newConfigKeys.put("Question skipped.volume", 0.6d);

            currentConfigVersion = 6;
        }

        if (!newConfigKeys.isEmpty()) {
            // iterate through all the new keys
            getConfig().set("Config Version", currentConfigVersion);
            for (Map.Entry<String, Object> entry : newConfigKeys.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();
                getConfig().set(key, value);
                saveConfig();
            }
        }

        Set<String> keys = messagesFile.getData().getKeys(false);

        // get rid of unused keys
        if (keys.contains("Winner Line")) {
            newLangKeys.put("Winner Line", null);
        }
        if (keys.contains("Trivia Over")) {
            newLangKeys.put("Trivia Over", null);
        }

        if (!newLangKeys.isEmpty()) {
            for (Map.Entry<String, Object> entry : newLangKeys.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();
                messagesFile.getData().set(key, value);
                messagesFile.saveData();
            }
        }
    }

    public PlayerDataContainer getStats() {
        return stats;
    }
}
