/*
 * Trivia by MarCarrot, 2020
 */

package me.marcarrots.triviatreasure.commands.subCommands;

import me.marcarrots.triviatreasure.TriviaTreasure;
import me.marcarrots.triviatreasure.commands.SubCommand;
import me.marcarrots.triviatreasure.language.Lang;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class Start extends SubCommand {

    public Start(TriviaTreasure plugin) {
        super(plugin);
    }

    @Override
    public String getName() {
        return "start";
    }

    @Override
    public String getDescription() {
        return Lang.COMMANDS_HELP_START.format_single();
    }

    @Override
    public String getSyntax() {
        return "start <# of rounds>";
    }

    @Override
    public String getPermission() {
        return "trivia.admin";
    }

    @Override
    public boolean perform(CommandSender commandSender, String[] args) {
        if (triviaTreasure.getGameManager().isGameRunning()) {
            commandSender.sendMessage(Lang.COMMANDS_ERROR_GAME_IN_PROGRESS.format_single());
            return true;
        }

        String param1 = null;
        if (args.length == 2) {
            param1 = args[1];
        }

        int setRounds = triviaTreasure.getTriviaConfig().defaultRounds;
        if (param1 != null && param1.trim().matches("-?\\d+")) {
            setRounds = Integer.parseInt(param1);
        }
        try {
            long timePerQuestion = triviaTreasure.getTriviaConfig().defaultTimePer;
            triviaTreasure.getGameManager().startGame(commandSender, timePerQuestion, setRounds, false);
        } catch (IllegalStateException e) {
            commandSender.sendMessage(ChatColor.RED + e.getMessage());
        }
        return true;
    }

}
