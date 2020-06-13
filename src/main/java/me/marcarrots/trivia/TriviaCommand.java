package me.marcarrots.trivia;

import me.marcarrots.trivia.listeners.ChatEvent;
import me.marcarrots.trivia.listeners.PlayerJoin;
import me.marcarrots.trivia.menu.subMenus.MainMenu;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TriviaCommand implements CommandExecutor {

    Trivia trivia;
    QuestionHolder questionHolder;
    private ChatEvent chatEvent;
    private final PlayerJoin playerJoin;

    public TriviaCommand(Trivia trivia, QuestionHolder questionHolder, ChatEvent chatEvent, PlayerJoin playerJoin) {
        this.trivia = trivia;
        this.questionHolder = questionHolder;
        this.chatEvent = chatEvent;
        this.playerJoin = playerJoin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {

        Player player;
        if (commandSender instanceof Player) {
            player = (Player) commandSender;
        } else {
            return false;
        }

        if (strings.length == 1 && strings[0].equalsIgnoreCase("reload")) {
            trivia.reloadConfig();
//            saveDefaultConfig();
//            config = getConfig();
//            config.options().copyDefaults(true);
//            saveConfig();
            trivia.parseFiles();
            player.sendMessage(ChatColor.GREEN + "Trivia files have been reloaded.");
            return false;
        }

        MainMenu menu = new MainMenu(trivia.getPlayerMenuUtility(player), trivia, questionHolder, chatEvent, playerJoin);
        menu.open();

        return false;
    }


}
