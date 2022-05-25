/*
 * Trivia by MarCarrot, 2020
 */

package me.marcarrots.trivia.commands.subCommands;

import me.marcarrots.trivia.Trivia;
import me.marcarrots.trivia.commands.SubCommand;
import me.marcarrots.trivia.language.Lang;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class Stop extends SubCommand {
    public Stop(Trivia plugin) {
        super(plugin);
    }

    @Override
    public String getName() {
        return "stop";
    }

    @Override
    public String getDescription() {
        return "Stops the current trivia game in progress.";
    }

    @Override
    public String getSyntax() {
        return "stop";
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
        trivia.getGame().stop();
        commandSender.sendMessage(Lang.GAME_HALTED.format_multiple(null));
        return false;
    }

}
