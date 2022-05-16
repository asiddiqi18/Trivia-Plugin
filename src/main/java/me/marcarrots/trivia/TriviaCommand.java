/*
 * Trivia by MarCarrot, 2020
 */

package me.marcarrots.trivia;

import me.marcarrots.trivia.language.Lang;
import me.marcarrots.trivia.menu.subMenus.MainMenu;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;

public class TriviaCommand implements CommandExecutor {

    final Trivia trivia;
    public TriviaCommand(Trivia trivia) {
        this.trivia = trivia;
    }

    private String helpFormat(String commandName, String commandDescription) {
        return String.format(ChatColor.GOLD + "/trivia %1$s" + ChatColor.WHITE + " - " + ChatColor.AQUA + "%2$s", commandName, commandDescription);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        String border = Lang.BORDER.format_single();
        if (strings.length == 0) {
            if (commandSender instanceof Player) {
                MainMenu menu = new MainMenu(trivia.getPlayerMenuUtility((Player) commandSender), trivia);
                menu.open();
            }
        } else {
            String prompt = strings[0];
            String param1 = null;
            if (strings.length == 2) {
                param1 = strings[1];
            }
            switch (prompt) {
                case "reload":
                    try {
                        commandSender.sendMessage(border);
                        trivia.reloadConfig();
                        commandSender.sendMessage(ChatColor.YELLOW + "- Config file reloaded.");
                        trivia.getQuestionHolder().readQuestions(trivia.getQuestionsFile());
                        commandSender.sendMessage(ChatColor.YELLOW + "- Questions file reloaded.");
                        trivia.loadConfigFile();
                        trivia.loadMessages();
                        commandSender.sendMessage(ChatColor.YELLOW + "- Messages file reloaded.");
                        trivia.loadRewards();
                        commandSender.sendMessage(ChatColor.YELLOW + "- Rewards file reloaded.");
                        commandSender.sendMessage(ChatColor.GREEN + "✔ Successfully reloaded all files!");
                    } catch (Exception e) {
                        commandSender.sendMessage(ChatColor.RED + "- Failed to reload files");
                        e.printStackTrace();
                    } finally {
                        commandSender.sendMessage(border);
                    }
                    return false;
                case "stop":
                    if (trivia.getGame() == null) {
                        commandSender.sendMessage(ChatColor.RED + "There is no trivia game in progress.");
                        return false;
                    }
                    trivia.getGame().stop();
                    commandSender.sendMessage(Lang.GAME_HALTED.format_multiple(null));
                    return false;
                case "start":
                    if (trivia.getGame() != null) {
                        commandSender.sendMessage(ChatColor.RED + "There is already a trivia game in progress.");
                        return false;
                    }
                    int setRounds = trivia.getConfig().getInt("Default rounds", 10);
                    if (param1 != null && param1.trim().matches("-?\\d+")) {
                        setRounds = Integer.parseInt(param1);
                    }
                    try {
                        long timePerQuestion = trivia.getConfig().getLong("Default time per round", 10L);
                        boolean doRepetition = false;
                        Game game = new Game(trivia, timePerQuestion, setRounds, doRepetition, commandSender);
                        trivia.setGame(game);
                        trivia.getGame().start();
                    } catch (IllegalAccessException e) {
                        commandSender.sendMessage(ChatColor.RED + e.getMessage());
                    }
                    return false;
                case "skip":
                    if (trivia.getGame() == null) {
                        commandSender.sendMessage(ChatColor.RED + "There is no trivia game in progress.");
                        return false;
                    }
                    if (!trivia.getGame().forceSkipRound()) {
                        commandSender.sendMessage(ChatColor.RED + "Failed to skip round. Reason: Skip request made in-between rounds.");
                    }
                    return false;
                case "version":
                    commandSender.sendMessage("Trivia version: " + trivia.getDescription().getVersion());
                    return false;
                case "stats":
                    if (commandSender instanceof Player) {
                        Player player = (Player) commandSender;
                        player.sendMessage(ChatColor.GREEN + "Trivia Stats for " + player.getName());
                        int playerWins = player.getPersistentDataContainer().getOrDefault(trivia.getNamespacedWinsKey(), PersistentDataType.INTEGER, 0) ;
                        player.sendMessage(ChatColor.GOLD + " - Number of games won: " + ChatColor.YELLOW + playerWins);
                        int playerAnswers = player.getPersistentDataContainer().getOrDefault(trivia.getNamespacedAnsweredKey(), PersistentDataType.INTEGER, 0) ;
                        player.sendMessage(ChatColor.GOLD + " - Number of questions answered: " + ChatColor.YELLOW + playerAnswers);
                    } else {
                        commandSender.sendMessage("This command is for players only.");
                    }
                    return false;
                default:
                    commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&e&m------------&e[ &6Trivia &e]&m------------"));
                    commandSender.sendMessage(helpFormat("", "(Main command) Opens up the in-game menu to start trivia or change settings."));
                    commandSender.sendMessage(helpFormat("start <# of rounds>", "Quickly starts up a trivia game. Optionally, specify number of rounds."));
                    commandSender.sendMessage(helpFormat("stop", "Stops the current trivia game in progress."));
                    commandSender.sendMessage(helpFormat("skip", "Skips a trivia question during a game."));
                    commandSender.sendMessage(helpFormat("reload", "Reloads all the trivia files."));
                    commandSender.sendMessage(helpFormat("help", "Displays this trivia help menu."));
                    commandSender.sendMessage(border);
                    return false;
            }

        }

        return false;
    }


}
