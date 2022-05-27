/*
 * Trivia by MarCarrot, 2020
 */

package me.marcarrots.trivia.commands;

import me.marcarrots.trivia.Trivia;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permissible;

public abstract class SubCommand {

    protected Trivia trivia;

    public SubCommand(Trivia trivia) {
        this.trivia = trivia;
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
