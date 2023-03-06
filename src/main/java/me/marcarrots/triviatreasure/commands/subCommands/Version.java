/*
 * Trivia by MarCarrot, 2020
 */

package me.marcarrots.triviatreasure.commands.subCommands;

import me.marcarrots.triviatreasure.TriviaTreasure;
import me.marcarrots.triviatreasure.commands.SubCommand;
import me.marcarrots.triviatreasure.language.Lang;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class Version extends SubCommand {
    public Version(TriviaTreasure plugin) {
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
        commandSender.sendMessage(ChatColor.GOLD + triviaTreasure.getDescription().getName() + " version: " + ChatColor.YELLOW + triviaTreasure.getDescription().getVersion());
        commandSender.sendMessage(ChatColor.GOLD + "Author: " + ChatColor.YELLOW + triviaTreasure.getDescription().getAuthors().get(0));
        commandSender.sendMessage(ChatColor.GOLD + "Website: " + ChatColor.YELLOW + triviaTreasure.getDescription().getWebsite());
        commandSender.sendMessage(ChatColor.GOLD + "API Version: " + ChatColor.YELLOW + triviaTreasure.getDescription().getAPIVersion());
        return false;
    }

}
