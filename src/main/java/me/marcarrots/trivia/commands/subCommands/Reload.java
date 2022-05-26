/*
 * Trivia by MarCarrot, 2020
 */

package me.marcarrots.trivia.commands.subCommands;

import me.marcarrots.trivia.Trivia;
import me.marcarrots.trivia.commands.SubCommand;
import me.marcarrots.trivia.language.Lang;
import org.bukkit.ChatColor;
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
            commandSender.sendMessage(ChatColor.YELLOW + "- Config file reloaded.");
            trivia.getQuestionHolder().readQuestions(trivia.getQuestionsFile());
            commandSender.sendMessage(ChatColor.YELLOW + "- Questions file reloaded.");
            trivia.loadConfigFile();
            trivia.loadMessages();
            commandSender.sendMessage(ChatColor.YELLOW + "- Messages file reloaded.");
            trivia.loadRewards();
            commandSender.sendMessage(ChatColor.YELLOW + "- Rewards file reloaded.");
            commandSender.sendMessage(ChatColor.GREEN + "✔ Successfully reloaded all files!");
        } catch (Exception e) {
            commandSender.sendMessage(ChatColor.RED + "- Failed to reload files");
            e.printStackTrace();
        } finally {
            commandSender.sendMessage(border);
        }
        return false;
    }

}
