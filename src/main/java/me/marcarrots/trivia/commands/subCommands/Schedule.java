package me.marcarrots.trivia.commands.subCommands;

import me.marcarrots.trivia.Trivia;
import me.marcarrots.trivia.commands.SubCommand;
import me.marcarrots.trivia.language.Lang;
import me.marcarrots.trivia.utils.Elapsed;
import org.bukkit.command.CommandSender;

public class Schedule extends SubCommand {
    public Schedule(Trivia trivia) {
        super(trivia);
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

        if (!trivia.getAutomatedGameManager().isSchedulingEnabled()) {
            commandSender.sendMessage(Lang.COMMANDS_SCHEDULE_DISABLED.format_single());
            return  false;
        }

        String time = Elapsed.millisToElapsedTime(trivia.getAutomatedGameManager().getNextAutomatedTimeFromNow()).getElapsedFormattedString();

        String s = Lang.COMMANDS_SCHEDULE.format_single();
        s = s.replace("%time%", time).replace("%players_needed%", String.valueOf(trivia.getAutomatedGameManager().getAutomatedPlayerReq()));

        commandSender.sendMessage(s);

        return false;
    }
}
