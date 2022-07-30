/*
 * Trivia by MarCarrot, 2020
 */

package me.marcarrots.trivia.commands.subCommands;

import me.marcarrots.trivia.Trivia;
import me.marcarrots.trivia.commands.SubCommand;
import me.marcarrots.trivia.language.Lang;
import org.bukkit.command.CommandSender;

public class Reload extends SubCommand {
    public Reload(Trivia trivia) {
        super(trivia);
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
            trivia.reloadConfig();
            commandSender.sendMessage(Lang.COMMANDS_RELOAD_CONFIG.format_single());
            trivia.getQuestionHolder().readQuestions(trivia);
            commandSender.sendMessage(Lang.COMMANDS_RELOAD_QUESTIONS.format_single());
            trivia.loadConfigFile();
            trivia.loadMessages();
            commandSender.sendMessage(Lang.COMMANDS_RELOAD_MESSAGES.format_single());
            trivia.loadRewards();
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
