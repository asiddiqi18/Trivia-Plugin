package me.marcarrots.trivia;

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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class Trivia extends JavaPlugin {

    private static Economy econ = null;
    private final QuestionHolder questionHolder = new QuestionHolder();
    private final ChatEvent chatEvent = new ChatEvent(this);
    private final PlayerJoin playerJoin = new PlayerJoin(this);
    private final HashMap<Player, PlayerMenuUtility> playerMenuUtilityMap = new HashMap<>();
    boolean schedulingEnabled;
    int automatedTime;
    int automatedPlayerReq;
    long lastAutomatedTime;
    long nextAutomatedTime;
    private int schedulerTask;
    private Rewards[] rewards;
    private Game game;

    public static Economy getEcon() {
        return econ;
    }

    public boolean isSchedulingEnabled() {
        return schedulingEnabled;
    }

    public void setSchedulingEnabled(boolean schedulingEnabled) {
        this.schedulingEnabled = schedulingEnabled;
    }

    public int getAutomatedPlayerReq() {
        return automatedPlayerReq;
    }

    public void setAutomatedPlayerReq(int automatedPlayerReq) {
        this.automatedPlayerReq = automatedPlayerReq;
    }

    public long getLastAutomatedTime() {
        return lastAutomatedTime;
    }

    public long getNextAutomatedTime() {
        return nextAutomatedTime;
    }

    public void setAutomatedTime(int automatedTime) {
        this.automatedTime = automatedTime;
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
        parseFiles();
        Lang.setFile(getConfig());
        game = null;
        MetricsLite metricsLite = new MetricsLite(this, 7912);
        getServer().getPluginManager().registerEvents(new InventoryClick(), this);
        getServer().getPluginManager().registerEvents(chatEvent, this);
        getServer().getPluginManager().registerEvents(playerJoin, this);
        getCommand("trivia").setExecutor(new TriviaCommand(this, questionHolder, chatEvent, playerJoin));
        if (!setupEconomy()) {
            Bukkit.getLogger().info("No vault has been detected, disabling vault features...");
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public void loadConfig() {
        schedulingEnabled = getConfig().getBoolean("Scheduled games", false);
        automatedTime = getConfig().getInt("Scheduled games interval", 60);
        automatedPlayerReq = getConfig().getInt("Scheduled games minimum players", 6);
        generateRewards();
        configUpdater();
        automatedSchedule();
        getConfig().options().copyDefaults(true);
        saveDefaultConfig();
//        Bukkit.getLogger().info(String.format("schedulingEnabled: %1$s, Scheduled games interval: %2$s, Scheduled games minimum players: %3$s",
//                schedulingEnabled, automatedTime, automatedPlayerReq));
    }

    public void parseFiles() {
        questionHolder.clear();
        List<String> unparsedQuestions = getConfig().getStringList("Questions and Answers");
        if (unparsedQuestions.size() == 0) {
            Bukkit.getLogger().info("There are no trivia questions loaded.");
            return;
        }
        for (String item : unparsedQuestions) {
            int posBefore = item.indexOf("/$/");
            if (posBefore == -1) {
                continue;
            }
            int posAfter = posBefore + 3;

            Question triviaQuestion = new Question();
            triviaQuestion.setQuestion(item.substring(0, posBefore).trim());
            triviaQuestion.setAnswer(item.substring(posAfter).trim());
            questionHolder.add(triviaQuestion);
        }

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
            Bukkit.getLogger().info("Stopped because disabled");
            return;
        }

        if (Bukkit.getOnlinePlayers().size() < automatedPlayerReq) {
            Bukkit.getLogger().info("Stopped because not enough online");
            return;
        }

        nextAutomatedTime = System.currentTimeMillis() + (automatedTime * 60 * 1000);

        BukkitScheduler scheduler = getServer().getScheduler();
        scheduler.cancelTask(schedulerTask);
        schedulerTask = scheduler.scheduleSyncRepeatingTask(this, () -> {
            ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
            Bukkit.dispatchCommand(console, "trivia start");
            nextAutomatedTime = System.currentTimeMillis() + (automatedTime * 60 * 1000);
        }, automatedTime * 20 * 60, automatedTime * 20 * 60);
        Bukkit.getLogger().info("Got over here.");


    }

    private void configUpdater() {

        Set<String> currentKeys = getConfig().getKeys(false);
        HashMap<String, Object> newKeys = new HashMap<>();

        // if they have version 1 of the config...
        if (getConfig().getInt("Config Version", 1) == 1) {
            newKeys.put("Scheduled games", schedulingEnabled);
            newKeys.put("Scheduled games interval", automatedTime);
            newKeys.put("Scheduled games minimum players", automatedPlayerReq);
            newKeys.put("Default time per round", 15);
        }

        // iterate through all the new keys
        for (Map.Entry<String, Object> entry : newKeys.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            getConfig().set(key, value);
            saveConfig();
        }


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
