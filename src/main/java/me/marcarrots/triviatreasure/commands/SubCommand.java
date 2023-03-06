/*
 * Trivia by MarCarrot, 2020
 */

package me.marcarrots.triviatreasure.commands;

import me.marcarrots.triviatreasure.TriviaTreasure;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permissible;

public abstract class SubCommand {

    protected TriviaTreasure triviaTreasure;

    public SubCommand(TriviaTreasure triviaTreasure) {
        this.triviaTreasure = triviaTreasure;
    }

    public abstract String getName();

    public abstract String getDescription();

    public abstract String getSyntax();

    public abstract String getPermission();

    public abstract boolean perform(CommandSender commandSender, String[] args);

    public boolean hasPermission(Permissible permissible) {
        return permissible.hasPermission(getPermission());
    }

}
