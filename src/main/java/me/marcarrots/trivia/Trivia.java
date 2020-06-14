package me.marcarrots.trivia;

import me.marcarrots.trivia.listeners.ChatEvent;
import me.marcarrots.trivia.listeners.InventoryClick;
import me.marcarrots.trivia.listeners.PlayerJoin;
import me.marcarrots.trivia.menu.PlayerMenuUtility;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.List;

public final class Trivia extends JavaPlugin {

    private final QuestionHolder questionHolder = new QuestionHolder();
    private final ChatEvent chatEvent = new ChatEvent();
    private final PlayerJoin playerJoin = new PlayerJoin();
    private final HashMap<Player, PlayerMenuUtility> playerMenuUtilityMap = new HashMap<>();
    private boolean gameActive;

    public boolean isGameActive() {
        return gameActive;
    }

    public void setGameActive(boolean gameActive) {
        this.gameActive = gameActive;
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
        gameActive = false;
        getServer().getPluginManager().registerEvents(new InventoryClick(), this);
        getServer().getPluginManager().registerEvents(chatEvent, this);
        getServer().getPluginManager().registerEvents(playerJoin, this);
        getCommand("trivia").setExecutor(new TriviaCommand(this, questionHolder, chatEvent, playerJoin));
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


}
