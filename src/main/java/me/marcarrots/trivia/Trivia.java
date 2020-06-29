package me.marcarrots.trivia;

import me.marcarrots.trivia.listeners.ChatEvent;
import me.marcarrots.trivia.listeners.InventoryClick;
import me.marcarrots.trivia.listeners.PlayerJoin;
import me.marcarrots.trivia.menu.PlayerMenuUtility;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public final class Trivia extends JavaPlugin {

    private final QuestionHolder questionHolder = new QuestionHolder();
    private final ChatEvent chatEvent = new ChatEvent(this);
    private final PlayerJoin playerJoin = new PlayerJoin(this);
    private final HashMap<Player, PlayerMenuUtility> playerMenuUtilityMap = new HashMap<>();
    private Rewards[] rewards;
    private Game game;
    private static Economy econ = null;

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
        if (!setupEconomy() ) {
            Bukkit.getLogger().info(String.format("[%s] - No Vault Detected, Disabling Vault Integration...", getDescription().getName()));
            return;
        }
        generateRewards();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    private void loadConfig() {
        getConfig().options().copyDefaults(true);
        saveDefaultConfig();
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
        return econ != null;
    }

    public boolean vaultEnabled() {
        return getServer().getPluginManager().getPlugin("Vault") != null;
    }


}
