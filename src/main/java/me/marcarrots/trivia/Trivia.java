/*
 * Trivia by MarCarrot, 2020
 */

package me.marcarrots.trivia;

import me.marcarrots.trivia.data.QuestionFileManager;
import me.marcarrots.trivia.listeners.ChatEvent;
import me.marcarrots.trivia.listeners.InventoryClick;
import me.marcarrots.trivia.listeners.PlayerJoin;
import me.marcarrots.trivia.menu.PlayerMenuUtility;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

public final class Trivia extends JavaPlugin {

    private static Economy econ = null;
    private final QuestionHolder questionHolder = new QuestionHolder();
    private final ChatEvent chatEvent = new ChatEvent(this);
    private final PlayerJoin playerJoin = new PlayerJoin(this);
    private final HashMap<Player, PlayerMenuUtility> playerMenuUtilityMap = new HashMap<>();
    boolean schedulingEnabled;
    int automatedTime;
    int automatedPlayerReq;
    long nextAutomatedTime;
    private int largestQuestionNum = 0;
    private int schedulerTask;
    private Rewards[] rewards;
    private Game game;

    private QuestionFileManager questionsFile;

    public static Economy getEcon() {
        return econ;
    }

    public QuestionFileManager getQuestionsFile() {
        return questionsFile;
    }

    public boolean isSchedulingEnabled() {
        return schedulingEnabled;
    }

    public int getAutomatedPlayerReq() {
        return automatedPlayerReq;
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
        loadConfig();
        this.questionsFile = new QuestionFileManager(this, "questions.yml");
        for (String questionNum : questionsFile.getData().getKeys(false)) {
            try {
                if (Integer.parseInt(questionNum) > largestQuestionNum)
                    largestQuestionNum = Integer.parseInt(questionNum);
            } catch (NumberFormatException e) {
                Bukkit.getLogger().log(Level.WARNING, String.format("The key '%s' is invalid and cannot be interpreted.", questionNum));
            }
        }

        parseFiles();
        configUpdater();
        game = null;
        MetricsLite metricsLite = new MetricsLite(this, 7912);
        getServer().getPluginManager().registerEvents(new InventoryClick(), this);
        getServer().getPluginManager().registerEvents(chatEvent, this);
        getServer().getPluginManager().registerEvents(playerJoin, this);
        getCommand("trivia").setExecutor(new TriviaCommand(this, questionHolder));
        if (!setupEconomy()) {
            Bukkit.getLogger().info("No vault has been detected, disabling vault features...");
        }

        UpdateChecker.init(this, 80401).requestUpdateCheck().whenComplete((result, e) -> {
            if (result.requiresUpdate()) {
                getLogger().info(String.format("[TriviaGUI] There is a new version of TriviaGUI released!(version %s). Download it at: %s",
                        result.getNewestVersion(),
                        "https://www.spigotmc.org/resources/triviagui.80401/"));
            }
        });

    }

    @Override
    public void onDisable() {

    }

    public void loadConfig() {
        schedulingEnabled = getConfig().getBoolean("Scheduled games", false);
        automatedTime = getConfig().getInt("Scheduled games interval", 60);
        automatedPlayerReq = getConfig().getInt("Scheduled games minimum players", 6);
        generateRewards();
        automatedSchedule();
        getConfig().options().copyDefaults(true);
        saveDefaultConfig();
        Lang.setFile(getConfig());
    }

    public void addQuestion(String question, List<String> answer) {
        HashMap<String, Object> questionMap = new HashMap<>();
        questionMap.put("question", question);
        questionMap.put("answer", answer);
        questionsFile.getData().createSection(String.valueOf(largestQuestionNum++), questionMap);
        questionsFile.saveData();
    }

    public void parseFiles() {

        questionHolder.clear();
        if (getConfig().contains("Questions and Answers")) {
            Bukkit.getLogger().log(Level.INFO, "Migrating old data to new data...");
            List<String> unparsedQuestions = getConfig().getStringList("Questions and Answers");
            if (unparsedQuestions.size() != 0)
                for (String item : unparsedQuestions) {
                    int posBefore = item.indexOf("/$/");
                    if (posBefore == -1)
                        continue;
                    int posAfter = posBefore + 3;
                    addQuestion(item.substring(0, posBefore).trim(), Collections.singletonList(item.substring(posAfter).trim()));
                }
            getConfig().set("Questions and Answers", null);
            saveConfig();
        }
        questionsFile.reloadFiles();
        questionsFile.getData().getKeys(false).forEach(key -> {
            try {
                Question triviaQuestion = new Question();
                triviaQuestion.setId(Integer.parseInt(key));
                triviaQuestion.setQuestion(questionsFile.getData().getString(key + ".question"));
                triviaQuestion.setAnswer(questionsFile.getData().getStringList(key + ".answer"));
                this.questionHolder.add(triviaQuestion);
            } catch (NumberFormatException | NullPointerException e) {
                Bukkit.getLogger().log(Level.SEVERE, String.format("Error with interpreting '%s': Invalid ID. (%s)", key, e.getMessage()));
            }
        });

    }

    private void generateRewards() {
        int rewardAmt = 3;
        rewards = new Rewards[rewardAmt];
        for (int i = 0; i < rewardAmt; i++) {
            rewards[i] = new Rewards(this, i);
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

    private void automatedSchedule() {

        if (!schedulingEnabled) {
            return;
        }

        nextAutomatedTime = System.currentTimeMillis() + (automatedTime * 60 * 1000);

        BukkitScheduler scheduler = getServer().getScheduler();
        scheduler.cancelTask(schedulerTask);
        schedulerTask = scheduler.scheduleSyncRepeatingTask(this, () -> {
            if (Bukkit.getOnlinePlayers().size() < automatedPlayerReq) {
                return;
            }
            ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
            Bukkit.getLogger().info("Automated Trivia Beginning...");
            Bukkit.dispatchCommand(console, "trivia start");
            nextAutomatedTime = System.currentTimeMillis() + (automatedTime * 60 * 1000);
        }, automatedTime * 20 * 60, automatedTime * 20 * 60);

    }

    private void configUpdater() {

        HashMap<String, Object> newKeys = new HashMap<>();
        int currentConfigVersion = getConfig().getInt("Config Version", 1);
        // if they have version 1 of the config...

        if (currentConfigVersion == 1) {
            newKeys.put("Scheduled games", schedulingEnabled);
            newKeys.put("Scheduled games interval", automatedTime);
            newKeys.put("Scheduled games minimum players", automatedPlayerReq);
            newKeys.put("Time between rounds", 2);
            currentConfigVersion = 2;
        }

        if (currentConfigVersion == 2) {
            newKeys.put(Lang.SKIP.getPath(), Lang.SKIP.getDefault());
        }

        // iterate through all the new keys
        for (Map.Entry<String, Object> entry : newKeys.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            getConfig().set(key, value);
            saveConfig();
        }

        getConfig().set("Config Version", 3);
        saveConfig();

    }

    public String getTimeUntilScheduled() {

        long durationInMillis = nextAutomatedTime - System.currentTimeMillis();

        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;

        long elapsedDays = durationInMillis / daysInMilli;
        durationInMillis = durationInMillis % daysInMilli;

        long elapsedHours = durationInMillis / hoursInMilli;
        durationInMillis = durationInMillis % hoursInMilli;

        long elapsedMinutes = durationInMillis / minutesInMilli;
        durationInMillis = durationInMillis % minutesInMilli;

        long elapsedSeconds = durationInMillis / secondsInMilli;

        return String.format("%02d days, %02d hours, %02d minutes, %02d seconds", elapsedDays, elapsedHours, elapsedMinutes, elapsedSeconds);
    }


}
