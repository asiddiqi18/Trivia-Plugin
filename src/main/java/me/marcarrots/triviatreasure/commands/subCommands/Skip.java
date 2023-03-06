/*
 * Trivia by MarCarrot, 2020
 */

package me.marcarrots.triviatreasure.commands.subCommands;

import me.marcarrots.triviatreasure.TriviaTreasure;
import me.marcarrots.triviatreasure.commands.SubCommand;
import me.marcarrots.triviatreasure.language.Lang;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class Skip extends SubCommand {
    public Skip(TriviaTreasure plugin) {
        super(plugin);
    }

    @Override
    public String getName() {
        return "skip";
    }

    @Override
    public String getDescription() {
        return Lang.COMMANDS_HELP_SKIP.format_single();
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
        if (triviaTreasure.getGame() == null) {
            commandSender.sendMessage(Lang.COMMANDS_ERROR_GAME_NOT_IN_PROGRESS.format_single());
            return false;
        }
        if (!triviaTreasure.getGame().forceSkipRound()) {
            commandSender.sendMessage(ChatColor.RED + "Failed to skip round. Reason: Skip request made in-between rounds.");
        }
        return false;
    }

}
