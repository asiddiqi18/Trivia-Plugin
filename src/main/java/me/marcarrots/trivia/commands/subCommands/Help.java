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
        return Lang.COMMANDS_HELP_HELP.format_single();
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


        if (commandSender.hasPermission("trivia.admin")) {
            commandSender.sendMessage(Lang.COMMANDS_HELP_PREFIX.format_single() + Lang.COMMANDS_HELP_MAIN.format_single());
        }

        for (SubCommand sc : subCommands) {
            if (commandSender.hasPermission(sc.getPermission())) {
                if (sc.getName().equals("schedule") && !trivia.getAutomatedGameManager().isSchedulingEnabled()) {
                       continue;
                }
                commandSender.sendMessage(Lang.COMMANDS_HELP_PREFIX.format_single() + sc.getDescription());

            }
        }
        commandSender.sendMessage(Lang.BORDER.format_single());

        return false;
    }

}
