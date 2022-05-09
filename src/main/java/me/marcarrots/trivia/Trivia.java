/*
 * Trivia by MarCarrot, 2020
 */

package me.marcarrots.trivia;

import me.marcarrots.trivia.api.UpdateChecker;
import me.marcarrots.trivia.data.FileManager;
import me.marcarrots.trivia.language.Lang;
import me.marcarrots.trivia.listeners.ChatEvent;
import me.marcarrots.trivia.listeners.EntityDamage;
import me.marcarrots.trivia.listeners.InventoryClick;
import me.marcarrots.trivia.listeners.PlayerJoin;
import me.marcarrots.trivia.menu.PlayerMenuUtility;
import me.marcarrots.trivia.schedulers.GameScheduler;
import me.marcarrots.trivia.schedulers.IntervalScheduler;
import me.marcarrots.trivia.schedulers.TimeOfDayScheduler;
import net.milkbowl.vault.economy.Economy;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;
import java.util.logging.Level;

public final class Trivia extends JavaPlugin {

    private static Economy econ = null;
    private final QuestionHolder questionHolder = new QuestionHolder();
    private final HashMap<Player, PlayerMenuUtility> playerMenuUtilityMap = new HashMap<>();
    private int largestQuestionNum = 0;
    private Rewards[] rewards;
    private Game game = null;
    private FileManager questionsFile;
    private FileManager messagesFile;
    private FileManager rewardsFile;
    private String updateNotice = null;
    private GameScheduler automatedGameManager;

    public static Economy getEcon() {
        return econ;
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

    public GameScheduler getAutomatedGameManager() {
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
            playerMenuUtility = new PlayerMenuUtility(getConfig(), player);
            playerMenuUtilityMap.put(player, playerMenuUtility);
            return playerMenuUtility;
        }
    }

    @Override
    public void onEnable() {

        // load stuff from files
        loadConfigFile();
        loadQuestions();
        loadMessages();
        loadRewards();

        // bStats
        new Metrics(this, 7912);

        // register listeners and commands
        getServer().getPluginManager().registerEvents(new InventoryClick(), this);
        getServer().getPluginManager().registerEvents(new ChatEvent(this),this);
        getServer().getPluginManager().registerEvents(new PlayerJoin(this),this);
        getServer().getPluginManager().registerEvents(new EntityDamage(),this);
        getCommand("trivia").setExecutor(new TriviaCommand(this, questionHolder));

        // check for soft-dependencies
        if (!setupEconomy()) {
            Bukkit.getLogger().info("[Trivia!] No vault has been detected, disabling vault features...");
        }

        // check for updates
        new UpdateChecker(this, 80401).getVersion(newVersion -> {
            String currentVersion = getDescription().getVersion();
            if (Integer.parseInt(newVersion.substring(2)) > Integer.parseInt(currentVersion.substring(2))) {
                updateNotice = String.format("%s - There is a new version available for Trivia (new version: %s, current version: %s)! Get it at: %s.",
                        ChatColor.AQUA + "[Trivia!]" + ChatColor.YELLOW,
                        ChatColor.GREEN + newVersion + ChatColor.YELLOW,
                        ChatColor.GREEN + currentVersion + ChatColor.YELLOW,
                        ChatColor.WHITE + "https://www.spigotmc.org/resources/trivia-easy-to-setup-game-%C2%BB-custom-rewards-%C2%BB-in-game-gui-menus-more.80401/" + ChatColor.YELLOW);
                Bukkit.getLogger().info(ChatColor.stripColor(updateNotice));
            }
        });

        // update config for older versions
        configUpdater();
    }

    @Override
    public void onDisable() {
        if (game != null) {
            game.hideBossBar();
            game = null;
        }
    }

    public void loadConfigFile() {
        getConfig().options().copyDefaults(true);
        saveDefaultConfig();

        if (automatedGameManager != null) {
            automatedGameManager.cancel();
        }

        int schedulingMode = getConfig().getInt("Scheduled games", 0);

        if (schedulingMode == 1) {
            automatedGameManager = new IntervalScheduler(this);
        } else if (schedulingMode == 2) {
            automatedGameManager = new TimeOfDayScheduler(this);
        } else {
            return;
        }
        automatedGameManager.automatedSchedule();

    }

    public void loadQuestions() {
        questionsFile = new FileManager(this, "questions.yml");
        readQuestions();
    }


    public void loadMessages() {
        if (messagesFile == null) {
            messagesFile = new FileManager(this, "messages.yml");
        }
        // migrate old way of storing messages to new way
        if (getConfig().contains("Messages")) {
            Bukkit.getLogger().log(Level.INFO, "[Trivia] Migrating old message data to new data...");
            List<String> messageKeys = Arrays.asList(
                    "Trivia Start",
                    "Trivia Over",
                    "Winner Line",
                    "Winner List",
                    "No Winners",
                    "Solved Message",
                    "Question Time Up",
                    "Question Display",
                    "Question Skipped"
            );

            for (String key : messageKeys) {
                messagesFile.getData().set(key, getConfig().getString("Messages." + key, ""));
            }
            getConfig().set("Messages", null);
            saveConfig();
        }

        messagesFile.reloadFiles();

        Lang.setFile(messagesFile.getData());

        Lang[] langValues = Lang.values();

        // add new keys to file
        List<String> addedKeys = new ArrayList<>();
        for (Lang val : langValues) {
            if (!messagesFile.getData().getKeys(false).contains(val.getPath())) {
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
            Bukkit.getLogger().info("[Trivia!] The following keys were added to messages.yml: " + addedKeys);
            messagesFile.saveData();
        }
        if (removedKeys.size() > 0) {
            Bukkit.getLogger().info("[Trivia!] The following keys were removed from messages.yml: " + removedKeys);
            messagesFile.saveData();
        }

    }


    public void loadRewards() {
        if (rewardsFile == null) {
            rewardsFile = new FileManager(this, "rewards.yml");
        }
        if (getConfig().contains("Rewards")) {
            for (int i = 0; i < 4; i++) {
                if (getConfig().contains("Rewards." + i)) {
                    Bukkit.getLogger().log(Level.INFO, "[Trivia] Migrating old rewards data to new data...");
                    rewardsFile.getData().set(i + ".Money", getConfig().getDouble("Rewards." + i + ".Money"));
                    rewardsFile.getData().set(i + ".Experience", getConfig().getDouble("Rewards." + i + ".Experience"));
                    rewardsFile.getData().set(i + ".Message", getConfig().getString("Rewards." + i + ".Message"));
                    rewardsFile.getData().set(i + ".Items", getConfig().getList("Rewards." + i + ".Items"));
                    rewardsFile.saveData();
                }
            }
            getConfig().set("Rewards", null);
            saveConfig();
        }
        int rewardAmt = 4;
        rewardsFile.reloadFiles();
        rewards = new Rewards[rewardAmt];
        for (int i = 0; i < rewardAmt; i++) {
            rewards[i] = new Rewards(this, i);
        }
    }

    // save question as object
    public void readQuestions() {
        questionHolder.clear();
        if (getConfig().contains("Questions and Answers")) {
            Bukkit.getLogger().log(Level.INFO, "[Trivia] Migrating old question data to new data...");
            List<String> unparsedQuestions = getConfig().getStringList("Questions and Answers");
            if (unparsedQuestions.size() != 0)
                for (String item : unparsedQuestions) {
                    int posBefore = item.indexOf("/$/");
                    if (posBefore == -1)
                        continue;
                    int posAfter = posBefore + 3;
                    writeQuestions(item.substring(0, posBefore).trim(), Collections.singletonList(item.substring(posAfter).trim()), null);
                }
            getConfig().set("Questions and Answers", null);
            saveConfig();
        }

        questionsFile.reloadFiles();
        for (String key : questionsFile.getData().getKeys(false)) {
            try {
                Question triviaQuestion = new Question();
                triviaQuestion.setId(Integer.parseInt(key));
                extractLargestQuestionNum(key);
                triviaQuestion.setQuestion(questionsFile.getData().getString(key + ".question"));
                triviaQuestion.setAnswer(questionsFile.getData().getStringList(key + ".answer"));
                triviaQuestion.setAuthor(questionsFile.getData().getString(key + ".author"));
                questionHolder.add(triviaQuestion);
            } catch (NumberFormatException | NullPointerException e) {
                Bukkit.getLogger().log(Level.SEVERE, String.format("Error with interpreting '%s': Invalid ID. (%s)", key, e.getMessage()));
            }
        }
    }

    // write question to questions.yml
    public void writeQuestions(String question, List<String> answer, String author) {
        HashMap<String, Object> questionMap = new HashMap<>();
        questionMap.put("question", question);
        questionMap.put("answer", answer);
        if (author != null) {
            questionMap.put("author", author);
        }
        questionsFile.getData().createSection(String.valueOf(++largestQuestionNum), questionMap);
        questionsFile.saveData();
    }


    private void extractLargestQuestionNum(String questionNum) {
        try {
            if (Integer.parseInt(questionNum) > largestQuestionNum)
                largestQuestionNum = Integer.parseInt(questionNum);
        } catch (NumberFormatException e) {
            Bukkit.getLogger().log(Level.WARNING, String.format("The key '%s' is invalid and cannot be interpreted.", questionNum));
        }
    }


    private boolean setupEconomy() {
        if (!vaultEnabled()) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return true;
    }


    public boolean vaultEnabled() {
        return getServer().getPluginManager().getPlugin("Vault") != null;
    }


    // transform old config to new config
    private void configUpdater() {
        HashMap<String, Object> newConfigKeys = new HashMap<>();
        HashMap<String, Object> newLangKeys = new HashMap<>();
        int currentConfigVersion = getConfig().getInt("Config Version");
        // if they have version 1 of the config...
        if (currentConfigVersion <= 1) {
            newConfigKeys.put("Scheduled games", false);
            newConfigKeys.put("Scheduled games interval", 60);
            newConfigKeys.put("Scheduled games minimum players", 6);
            newConfigKeys.put("Time between rounds", 2);
            currentConfigVersion = 2;
        }

        if (currentConfigVersion == 2) {
            newConfigKeys.put(Lang.SKIP.getPath(), Lang.SKIP.getDefault());
        }

        if (currentConfigVersion == 3) {
            newConfigKeys.put("Enable boss bar", true);
            newLangKeys.put("Boss Bar Game Info", "Trivia Match (%questionNumber%/%totalQuestions%)");
            newLangKeys.put("Boss Bar Game Over", "Trivia is over!");
            newLangKeys.put("Boss Bar Thanks", "Thanks for playing!");
        }

        if (!newConfigKeys.isEmpty()) {
            // iterate through all the new keys
            getConfig().set("Config Version", 5);
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

}
