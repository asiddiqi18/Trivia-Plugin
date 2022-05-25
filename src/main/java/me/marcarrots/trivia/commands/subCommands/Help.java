/*
 * Trivia by MarCarrot, 2020
 */

package me.marcarrots.trivia.commands.subCommands;

import me.marcarrots.trivia.Trivia;
import me.marcarrots.trivia.commands.SubCommand;
import me.marcarrots.trivia.language.Lang;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class Help extends SubCommand {

    private List<SubCommand> subCommands = new ArrayList<>();

    public Help(Trivia plugin) {
        super(plugin);
    }

    public void setSubCommands(List<SubCommand> subCommands) {
        this.subCommands = subCommands;
    }

    @Override
    public String getName() {
        return "help";
    }

    @Override
    public String getDescription() {
        return "Displays a help menu describing trivia commands.";
    }

    @Override
    public String getSyntax() {
        return "help";
    }

    @Override
    public String getPermission() {
        return "trivia.player";
    }

    @Override
    public boolean perform(CommandSender commandSender, String[] args) {
        commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&e&m------------&e[ &6Trivia &e]&m------------"));
        commandSender.sendMessage(ChatColor.GOLD + "/trivia" + ChatColor.GRAY + " - " + ChatColor.WHITE + "(Main command) Opens up the Trivia menu to manage or start trivia.");

        for (SubCommand sc : subCommands) {
            if (commandSender.hasPermission(sc.getPermission())) {
                commandSender.sendMessage(String.format(ChatColor.GOLD + "/trivia %1$s" + ChatColor.GRAY + " - " + ChatColor.WHITE + "%2$s", sc.getSyntax(), sc.getDescription()));
            }
        }
        commandSender.sendMessage(Lang.BORDER.format_single());

        return false;
    }

}
