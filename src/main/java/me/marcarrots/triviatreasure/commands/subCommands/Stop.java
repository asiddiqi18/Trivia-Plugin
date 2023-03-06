/*
 * Trivia by MarCarrot, 2020
 */

package me.marcarrots.triviatreasure.commands.subCommands;

import me.marcarrots.triviatreasure.TriviaTreasure;
import me.marcarrots.triviatreasure.commands.SubCommand;
import me.marcarrots.triviatreasure.language.Lang;
import org.bukkit.command.CommandSender;

public class Stop extends SubCommand {
    public Stop(TriviaTreasure plugin) {
        super(plugin);
    }

    @Override
    public String getName() {
        return "stop";
    }

    @Override
    public String getDescription() {
        return Lang.COMMANDS_HELP_STOP.format_single();
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
        if (triviaTreasure.getGame() == null) {
            commandSender.sendMessage(Lang.COMMANDS_ERROR_GAME_NOT_IN_PROGRESS.format_single());
            return false;
        }
        triviaTreasure.getGame().stop();
        commandSender.sendMessage(Lang.GAME_HALTED.format_multiple(null));
        return false;
    }

}
