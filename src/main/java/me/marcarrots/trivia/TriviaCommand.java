/*
 * Trivia by MarCarrot, 2020
 */

package me.marcarrots.trivia;

import me.marcarrots.trivia.listeners.ChatEvent;
import me.marcarrots.trivia.listeners.PlayerJoin;
import me.marcarrots.trivia.menu.subMenus.MainMenu;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;

import static org.bukkit.Bukkit.getServer;

public class TriviaCommand implements CommandExecutor {

    private final PlayerJoin playerJoin;
    Trivia trivia;
    QuestionHolder questionHolder;
    private ChatEvent chatEvent;

    public TriviaCommand(Trivia trivia, QuestionHolder questionHolder, ChatEvent chatEvent, PlayerJoin playerJoin) {
        this.trivia = trivia;
        this.questionHolder = questionHolder;
        this.chatEvent = chatEvent;
        this.playerJoin = playerJoin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {


        if (strings.length == 1) {

            if (strings[0].equalsIgnoreCase("reload")) {
                trivia.reloadConfig();
                trivia.parseFiles();
                trivia.setSchedulingEnabled(trivia.getConfig().getBoolean("Scheduled games"));
                trivia.setAutomatedTime(trivia.getConfig().getInt("Scheduled games interval"));
                trivia.setAutomatedPlayerReq(trivia.getConfig().getInt("Scheduled games minimum players"));
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
