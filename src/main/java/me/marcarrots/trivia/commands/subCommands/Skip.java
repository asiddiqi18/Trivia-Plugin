/*
 * Trivia by MarCarrot, 2020
 */

package me.marcarrots.trivia.commands.subCommands;

import me.marcarrots.trivia.Trivia;
import me.marcarrots.trivia.commands.SubCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class Skip extends SubCommand {
    public Skip(Trivia plugin) {
        super(plugin);
    }

    @Override
    public String getName() {
        return "skip";
    }

    @Override
    public String getDescription() {
        return "Skips a trivia question during a game.";
    }

    @Override
    public String getSyntax() {
        return "skip";
    }

    @Override
    public String getPermission() {
        return "trivia.admin";
    }

    @Override
    public boolean perform(CommandSender commandSender, String[] args) {
        if (trivia.getGame() == null) {
            commandSender.sendMessage(ChatColor.RED + "There is no trivia game in progress.");
            return false;
        }
        if (!trivia.getGame().forceSkipRound()) {
            commandSender.sendMessage(ChatColor.RED + "Failed to skip round. Reason: Skip request made in-between rounds.");
        }
        return false;
    }

}
