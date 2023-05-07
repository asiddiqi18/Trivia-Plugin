/*
 * Trivia by MarCarrot, 2020
 */

package me.marcarrots.triviatreasure.commands.subCommands;

import me.marcarrots.triviatreasure.TriviaTreasure;
import me.marcarrots.triviatreasure.commands.SubCommand;
import me.marcarrots.triviatreasure.menu.subMenus.ListMenu;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Questions extends SubCommand {
    public Questions(TriviaTreasure triviaTreasure) {
        super(triviaTreasure);
    }

    @Override
    public String getName() {
        return "questions";
    }

    @Override
    public String getDescription() {
        return "Opens up questions menu to create/update questions or answers";
    }

    @Override
    public String getSyntax() {
        return "rewards";
    }

    @Override
    public String getPermission() {
        return "trivia.admin";
    }

    @Override
    public boolean perform(CommandSender commandSender, String[] args) {
        ListMenu menu = new ListMenu(triviaTreasure, (Player) commandSender);
        menu.open();
        return false;
    }
}
