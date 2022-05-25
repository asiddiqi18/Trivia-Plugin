/*
 * Trivia by MarCarrot, 2020
 */

package me.marcarrots.trivia.commands.subCommands;

import me.marcarrots.trivia.PlayerDataContainer;
import me.marcarrots.trivia.Trivia;
import me.marcarrots.trivia.commands.SubCommand;
import me.marcarrots.trivia.language.Lang;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.text.NumberFormat;

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
            String border = Lang.BORDER.format_single();
            player.sendMessage(border);
            player.sendMessage(ChatColor.GREEN + "Trivia stats for " + player.getName() + ":");
            player.sendMessage(ChatColor.GOLD + " - Number of games participated in: " + ChatColor.YELLOW + stats.getGamesParticipated(player));
            player.sendMessage(ChatColor.GOLD + " - Number of rounds won: " + ChatColor.YELLOW + stats.getRoundsWon(player));
            player.sendMessage(ChatColor.GOLD + " - Victories: ");
            player.sendMessage(ChatColor.GOLD + "   - 1st place: " + ChatColor.YELLOW + gamesWon[0]);
            player.sendMessage(ChatColor.GOLD + "   - 2nd place: " + ChatColor.YELLOW + gamesWon[1]);
            player.sendMessage(ChatColor.GOLD + "   - 3rd place: " + ChatColor.YELLOW + gamesWon[2]);
            player.sendMessage(ChatColor.GOLD + " - Enchanting exp earned from wins: " + ChatColor.YELLOW + NumberFormat.getIntegerInstance().format(stats.getExperienceWon(player)));
            if (trivia.isVaultEnabled()) {
                player.sendMessage(ChatColor.GOLD + " - Money earned from wins: " + ChatColor.YELLOW + NumberFormat.getCurrencyInstance().format(stats.getMoneyWon(player)));
            }
            player.sendMessage(border);
        } else {
            commandSender.sendMessage("This command is for players only.");
        }
        return false;
    }

}
