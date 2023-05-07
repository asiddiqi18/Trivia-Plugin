/*
 * Trivia by MarCarrot, 2020
 */

package me.marcarrots.triviatreasure.commands.subCommands;

import me.marcarrots.triviatreasure.TriviaTreasure;
import me.marcarrots.triviatreasure.commands.SubCommand;
import me.marcarrots.triviatreasure.language.Lang;
import me.marcarrots.triviatreasure.menu.subMenus.RewardsMainMenu;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Rewards extends SubCommand {
    public Rewards(TriviaTreasure triviaTreasure) {
        super(triviaTreasure);
    }

    @Override
    public String getName() {
        return "rewards";
    }

    @Override
    public String getDescription() {
        return Lang.COMMANDS_HELP_REWARDS.format_single();
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
        RewardsMainMenu menu = new RewardsMainMenu(triviaTreasure, (Player) commandSender);
        menu.open();
        return false;
    }
}
