/*
 * Trivia by MarCarrot, 2020
 */

package me.marcarrots.trivia.commands.subCommands;

import me.marcarrots.trivia.Trivia;
import me.marcarrots.trivia.commands.SubCommand;
import me.marcarrots.trivia.language.Lang;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class Version extends SubCommand {
    public Version(Trivia plugin) {
        super(plugin);
    }

    @Override
    public String getName() {
        return "version";
    }

    @Override
    public String getDescription() {
        return Lang.COMMANDS_HELP_VERSION.format_single();
    }

    @Override
    public String getSyntax() {
        return "version";
    }

    @Override
    public String getPermission() {
        return "trivia.admin";
    }

    @Override
    public boolean perform(CommandSender commandSender, String[] args) {
        commandSender.sendMessage(ChatColor.GOLD + trivia.getDescription().getName() + " version: " + ChatColor.YELLOW + trivia.getDescription().getVersion());
        commandSender.sendMessage(ChatColor.GOLD + "Author: " + ChatColor.YELLOW + trivia.getDescription().getAuthors().get(0));
        commandSender.sendMessage(ChatColor.GOLD + "Website: " + ChatColor.YELLOW + trivia.getDescription().getWebsite());
        commandSender.sendMessage(ChatColor.GOLD + "API Version: " + ChatColor.YELLOW + trivia.getDescription().getAPIVersion());
        return false;
    }

}
