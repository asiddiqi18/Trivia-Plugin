package me.marcarrots.triviatreasure.commands.subCommands;

import me.marcarrots.triviatreasure.TriviaTreasure;
import me.marcarrots.triviatreasure.commands.SubCommand;
import me.marcarrots.triviatreasure.language.Lang;
import me.marcarrots.triviatreasure.utils.Elapsed;
import org.bukkit.command.CommandSender;

public class Schedule extends SubCommand {
    public Schedule(TriviaTreasure triviaTreasure) {
        super(triviaTreasure);
    }

    @Override
    public String getName() {
        return "schedule";
    }

    @Override
    public String getDescription() {
        return Lang.COMMANDS_HELP_SCHEDULE.format_single();
    }

    @Override
    public String getSyntax() {
        return "schedule";
    }

    @Override
    public String getPermission() {
        return "trivia.player";
    }

    @Override
    public boolean perform(CommandSender commandSender, String[] args) {

        if (!triviaTreasure.getAutomatedGameManager().isSchedulingEnabled()) {
            commandSender.sendMessage(Lang.COMMANDS_SCHEDULE_DISABLED.format_single());
            return false;
        }

        String time = Elapsed.millisToElapsedTime(triviaTreasure.getAutomatedGameManager().getNextAutomatedTimeFromNow()).getElapsedFormattedString();

        String s = Lang.COMMANDS_SCHEDULE.format_single();
        s = s.replace("%time%", time).replace("%players_needed%", String.valueOf(triviaTreasure.getAutomatedGameManager().getAutomatedPlayerReq()));

        commandSender.sendMessage(s);

        return false;
    }
}
