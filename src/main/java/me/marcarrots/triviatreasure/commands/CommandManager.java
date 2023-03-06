/*
 * Trivia by MarCarrot, 2020
 */

package me.marcarrots.triviatreasure.commands;

import me.marcarrots.triviatreasure.TriviaTreasure;
import me.marcarrots.triviatreasure.commands.subCommands.*;
import me.marcarrots.triviatreasure.language.Lang;
import me.marcarrots.triviatreasure.menu.subMenus.MainMenu;
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
    private final TriviaTreasure triviaTreasure;
    Help help;

    public CommandManager(TriviaTreasure triviaTreasure) {
        this.triviaTreasure = triviaTreasure;
        subCommands.add(new Start(triviaTreasure));
        subCommands.add(new Skip(triviaTreasure));
        subCommands.add(new Stop(triviaTreasure));
        subCommands.add(new Stats(triviaTreasure));
        subCommands.add(new Reload(triviaTreasure));
        subCommands.add(new Version(triviaTreasure));

        subCommands.add(new Schedule(triviaTreasure));

        help = new Help(triviaTreasure);
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
                MainMenu menu = new MainMenu(triviaTreasure, (Player) commandSender);
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
                    commandSender.sendMessage(Lang.COMMANDS_ERROR_NO_PERMISSION.format_single());
                    return false;
                }
            }
        }

        commandSender.sendMessage(Lang.COMMANDS_ERROR_NOT_FOUND.format_single());
        help.perform(commandSender, args);
        return false;
    }

}
