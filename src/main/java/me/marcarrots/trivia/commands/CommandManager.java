/*
 * Trivia by MarCarrot, 2020
 */

package me.marcarrots.trivia.commands;

import me.marcarrots.trivia.Trivia;
import me.marcarrots.trivia.commands.subCommands.*;
import me.marcarrots.trivia.menu.subMenus.MainMenu;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class CommandManager implements CommandExecutor {

    private final List<SubCommand> subCommands = new ArrayList<>();
    private final Trivia trivia;
    Help help;

    public CommandManager(Trivia trivia) {
        this.trivia = trivia;
        subCommands.add(new Start(trivia));
        subCommands.add(new Skip(trivia));
        subCommands.add(new Stop(trivia));
        subCommands.add(new Stats(trivia));
        subCommands.add(new Reload(trivia));
        subCommands.add(new Version(trivia));

        help = new Help(trivia);
        help.setSubCommands(subCommands);
        subCommands.add(help);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, String[] args) {

        if (args.length == 0) {
            if (commandSender instanceof Player) {
                if (!commandSender.hasPermission("trivia.admin")) {
                    help.perform(commandSender, args);
                    return false;
                }
                MainMenu menu = new MainMenu(trivia, (Player) commandSender);
                menu.open();
            } else {
                commandSender.sendMessage(ChatColor.RED + "This command is meant for players. Use '/trivia help' for assistance.");
            }
            return false;
        }

        for (SubCommand subCommand : subCommands) {
            if (args[0].equalsIgnoreCase(subCommand.getName())) {
                if (subCommand.hasPermission(commandSender)) {
                    return subCommand.perform(commandSender, args);
                } else {
                    commandSender.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
                    return false;
                }
            }
        }

        commandSender.sendMessage(ChatColor.RED + "Command not found.");
        help.perform(commandSender, args);
        return false;
    }

}
