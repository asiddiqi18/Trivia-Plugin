/*
 * Trivia by MarCarrot, 2020
 */

package me.marcarrots.trivia.commands.subCommands;

import me.marcarrots.trivia.PlayerDataContainer;
import me.marcarrots.trivia.Trivia;
import me.marcarrots.trivia.commands.SubCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.text.NumberFormat;
import java.util.List;

public class Stats extends SubCommand {
    public Stats(Trivia plugin) {
        super(plugin);
    }

    @Override
    public String getName() {
        return "stats";
    }

    @Override
    public String getDescription() {
        return "Shows your trivia stats record.";
    }

    @Override
    public String getSyntax() {
        return "stats";
    }

    @Override
    public String getPermission() {
        return "trivia.player";
    }

    @Override
    public boolean perform(CommandSender commandSender, String[] args) {
        if (commandSender instanceof Player) {
            Player player = (Player) commandSender;
            PlayerDataContainer stats = trivia.getStats();
            int[] gamesWon = trivia.getStats().getGamesWon(player);
            player.sendMessage(ChatColor.GREEN + "Trivia stats for " + player.getName() + ":");
            player.sendMessage(ChatColor.GOLD + " - Number of games participated in: " + ChatColor.YELLOW + stats.getGamesParticipated(player));
            player.sendMessage(ChatColor.GOLD + " - Number of rounds won: " + ChatColor.YELLOW + stats.getRoundsWon(player));
            player.sendMessage(ChatColor.GOLD + " - Victories: ");
            player.sendMessage(ChatColor.GOLD + "   - 1st place: " + ChatColor.YELLOW + gamesWon[0]);
            player.sendMessage(ChatColor.GOLD + "   - 2nd place: " + ChatColor.YELLOW + gamesWon[1]);
            player.sendMessage(ChatColor.GOLD + "   - 3rd place: " + ChatColor.YELLOW + gamesWon[2]);
            if (trivia.isVaultEnabled()) {
                player.sendMessage(ChatColor.GOLD + " - Money earned from winning: " + ChatColor.YELLOW + NumberFormat.getCurrencyInstance().format(stats.getMoneyWon(player)));
            }
        } else {
            commandSender.sendMessage("This command is for players only.");
        }
        return false;
    }

    @Override
    public List<String> getTabSuggester(CommandSender commandSender, int argsLength) {
        return null;
    }
}
