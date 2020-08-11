/*
 * Trivia by MarCarrot, 2020
 */

package me.marcarrots.trivia;

import me.marcarrots.trivia.menu.subMenus.MainMenu;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TriviaCommand implements CommandExecutor {

    Trivia trivia;
    QuestionHolder questionHolder;

    public TriviaCommand(Trivia trivia, QuestionHolder questionHolder) {
        this.trivia = trivia;
        this.questionHolder = questionHolder;

    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {


        if (strings.length == 1) {

            if (strings[0].equalsIgnoreCase("reload")) {
                trivia.reloadConfig();
                trivia.parseFiles();
                trivia.loadConfig();
                commandSender.sendMessage(ChatColor.GREEN + "Trivia files have been reloaded.");
            } else if (strings[0].equalsIgnoreCase("stop")) {
                if (trivia.getGame() == null) {
                    commandSender.sendMessage(ChatColor.RED + "There is no trivia game in progress.");
                    return false;
                }
                trivia.getGame().stop();
                commandSender.sendMessage(ChatColor.RED + "Trivia has been forcibly halted.");
            } else if (strings[0].equalsIgnoreCase("start")) {
                if (trivia.getGame() != null) {
                    commandSender.sendMessage(ChatColor.RED + "There is already a trivia game in progress.");
                    return false;
                }
                trivia.setGame(new Game(trivia, questionHolder, commandSender));
                trivia.getGame().start();
            }
            return false;

        }

        if (commandSender instanceof Player) {
            MainMenu menu = new MainMenu(trivia.getPlayerMenuUtility((Player) commandSender), trivia, questionHolder);
            menu.open();
        }

        return false;
    }


}
