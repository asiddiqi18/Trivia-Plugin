/*
 * Trivia by MarCarrot, 2020
 */

package me.marcarrots.triviatreasure.commands.subCommands;

import me.marcarrots.triviatreasure.TriviaTreasure;
import me.marcarrots.triviatreasure.commands.SubCommand;
import me.marcarrots.triviatreasure.language.Lang;
import org.bukkit.command.CommandSender;

public class Reload extends SubCommand {
    public Reload(TriviaTreasure triviaTreasure) {
        super(triviaTreasure);
    }

    @Override
    public String getName() {
        return "reload";
    }

    @Override
    public String getDescription() {
        return Lang.COMMANDS_HELP_RELOAD.format_single();
    }

    @Override
    public String getSyntax() {
        return "reload";
    }

    @Override
    public String getPermission() {
        return "trivia.admin";
    }

    @Override
    public boolean perform(CommandSender commandSender, String[] args) {
        String border = Lang.BORDER.format_single();
        try {
            commandSender.sendMessage(border);
            triviaTreasure.reloadConfig();
            commandSender.sendMessage(Lang.COMMANDS_RELOAD_CONFIG.format_single());
            triviaTreasure.getQuestionHolder().readQuestions(triviaTreasure);
            commandSender.sendMessage(Lang.COMMANDS_RELOAD_QUESTIONS.format_single());
            triviaTreasure.loadConfigFile();
            triviaTreasure.loadMessages();
            commandSender.sendMessage(Lang.COMMANDS_RELOAD_MESSAGES.format_single());
            triviaTreasure.loadRewards();
            commandSender.sendMessage(Lang.COMMANDS_RELOAD_REWARDS.format_single());
            commandSender.sendMessage(Lang.COMMANDS_RELOAD_SUCCESSFUL.format_single());
        } catch (Exception e) {
            commandSender.sendMessage(Lang.COMMANDS_RELOAD_FAILURE.format_single());
            e.printStackTrace();
        } finally {
            commandSender.sendMessage(border);
        }
        return false;
    }

}
