/*
 * Trivia by MarCarrot, 2020
 */

package me.marcarrots.trivia;

import me.marcarrots.trivia.menu.subMenus.MainMenu;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class TriviaCommand implements CommandExecutor {

    Trivia trivia;
    QuestionHolder questionHolder;

    public TriviaCommand(Trivia trivia, QuestionHolder questionHolder) {
        this.trivia = trivia;
        this.questionHolder = questionHolder;

    }

    private String helpFormat(String commandName, String commandDescription) {
        return String.format(ChatColor.GOLD + "/trivia %1$s" + ChatColor.WHITE + " - " + ChatColor.AQUA + "%2$s", commandName, commandDescription);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {


        if (strings.length == 1) {
            String prompt = strings[0];

            switch (prompt) {
                case "reload":
                    try {
                        trivia.reloadConfig();
                        trivia.parseFiles();
                        trivia.loadConfig();
                        commandSender.sendMessage(ChatColor.GREEN + "Trivia files have been reloaded.");
                    } catch (Exception e) {
                        commandSender.sendMessage(ChatColor.RED + "Failed to reload files");
                        e.printStackTrace();
                    }
                    return false;
                case "stop":
                    if (trivia.getGame() == null) {
                        commandSender.sendMessage(ChatColor.RED + "There is no trivia game in progress.");
                        return false;
                    }
                    trivia.getGame().stop();
                    commandSender.sendMessage(ChatColor.RED + "Trivia has been forcibly halted.");
                    return false;
                case "start":
                    if (trivia.getGame() != null) {
                        commandSender.sendMessage(ChatColor.RED + "There is already a trivia game in progress.");
                        return false;
                    }
                    trivia.setGame(new Game(trivia, questionHolder, commandSender));
                    trivia.getGame().start();
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
                default:
                    commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&e&m------------&e[ &6Trivia &e]&m------------"));
                    commandSender.sendMessage(helpFormat("", "Opens up the in-game menu to start trivia or change settings."));
                    commandSender.sendMessage(helpFormat("start", "Quickly starts up a trivia game."));
                    commandSender.sendMessage(helpFormat("stop", "Stops the current trivia game in progress."));
                    commandSender.sendMessage(helpFormat("skip", "Skips a trivia question during a game."));
                    commandSender.sendMessage(helpFormat("reload", "Reloads the trivia config files."));
                    commandSender.sendMessage(helpFormat("help", "Displays this trivia help menu."));
//                    commandSender.sendMessage("Current Version: " + trivia.getDescription().getVersion());
                    commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&e&m------------------------------"));
                    return false;
            }


        }

        if (commandSender instanceof Player) {
            MainMenu menu = new MainMenu(trivia.getPlayerMenuUtility((Player) commandSender), trivia, questionHolder);
            menu.open();
        }

        return false;
    }


}
