package me.marcarrots.trivia;

import me.marcarrots.trivia.listeners.ChatEvent;
import me.marcarrots.trivia.menu.subMenus.MainMenu;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TriviaCommand implements CommandExecutor {

    Trivia trivia;
    QuestionHolder questionHolder;
    private ChatEvent chatEvent;

    public TriviaCommand(Trivia trivia, QuestionHolder questionHolder, ChatEvent chatEvent) {
        this.trivia = trivia;
        this.questionHolder = questionHolder;
        this.chatEvent = chatEvent;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {

        Player player;
        if (commandSender instanceof Player) {
            player = (Player) commandSender;
        } else {
            return false;
        }

        MainMenu menu = new MainMenu(trivia.getPlayerMenuUtility(player), trivia, questionHolder, chatEvent);
        menu.open();

        return false;
    }



}
