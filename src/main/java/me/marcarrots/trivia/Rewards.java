/*
 * Trivia by MarCarrot, 2020
 */

package me.marcarrots.trivia;

import me.marcarrots.trivia.language.Lang;
import me.marcarrots.trivia.language.Placeholder;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitScheduler;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import static org.bukkit.Bukkit.getServer;

// Class to deliver rewards for top winners.

public class Rewards {

    Trivia trivia;
    int place;
    int experience;
    double money;
    String message;
    List<ItemStack> items;
    List<String> commands;

    public Rewards(Trivia trivia, int place) {
        items = new ArrayList<>();
        this.trivia = trivia;
        this.place = place;
        experience = 0;
        getValuesFromRewardsFile();
    }

    public int getExperience() {
        return experience;
    }

    public void setExperience(int experience) {
        this.experience = experience;
        trivia.getRewardsFile().getData().set(place + ".Experience", experience);
        trivia.getRewardsFile().saveData();
    }

    public String getMessage() {
        return message != null ? ChatColor.translateAlternateColorCodes('&', message) : null;
    }

    public void setMessage(String message) {
        this.message = message;
        trivia.getRewardsFile().getData().set(place + ".Message", message);
        trivia.getRewardsFile().saveData();
    }

    public double getMoney() {
        return money;
    }

    public void setMoney(double money) {
        if (money < 0) {
            money *= -1;
        }
        this.money = money;
        trivia.getRewardsFile().getData().set(place + ".Money", money);
        trivia.getRewardsFile().saveData();
    }

    public List<ItemStack> getItems() {
        return items;
    }

    public void setItems(List<ItemStack> items) {
        this.items = items;
        trivia.getRewardsFile().getData().set(place + ".Items", items);
    }

    private void getValuesFromRewardsFile() {
        money = trivia.getRewardsFile().getData().getDouble(place + ".Money");
        experience = trivia.getRewardsFile().getData().getInt(place + ".Experience");
        message = trivia.getRewardsFile().getData().getString(place + ".Message");
        items = (List<ItemStack>) trivia.getRewardsFile().getData().get(place + ".Items");
        commands = (List<String>) trivia.getRewardsFile().getData().get(place + ".Commands");
    }

    public void giveReward(Player player) {

        // send money to player if vault is enabled
        if (trivia.vaultEnabled() && money > 0) {
            EconomyResponse r = Trivia.getEcon().depositPlayer(player, getMoney());
            if (!r.transactionSuccess()) {
                player.sendMessage(String.format("An error occurred: %s", r.errorMessage));
            }
        }

        if (commands != null) {
            for (String command : commands) {
                if (command.length() > 0) {
                    command = Lang.fillPlaceholders(new Placeholder.PlaceholderBuilder().player(player).build(), command);
                    Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(), command);
                }
            }
        }

        // send experience to player
        if (experience != 0) {
            player.giveExp(getExperience());
        }

        // send reward message to player if there is one
        if (message != null && !message.equalsIgnoreCase("none")) {
            BukkitScheduler scheduler = getServer().getScheduler();
            String formattedMessage = getMessage();
            int itemsIndex = formattedMessage.indexOf("%items%");
            String itemsColor = ChatColor.getLastColors(formattedMessage.substring(0, itemsIndex));
            formattedMessage = formattedMessage.replace("%items%", itemsToString(items, itemsColor));
            String moneyFormatted = NumberFormat.getIntegerInstance().format(money);
            formattedMessage = formattedMessage.replace("%money%", moneyFormatted);
            String experienceFormatted = NumberFormat.getIntegerInstance().format(experience);
            formattedMessage = formattedMessage.replace("%experience%", experienceFormatted);

            String finalFormattedMessage = formattedMessage;
            scheduler.scheduleSyncDelayedTask(trivia, () -> player.sendMessage(finalFormattedMessage), 3);
        }

        // if no item rewards are included, exit
        if (items == null || items.size() == 0) {
            return;
        }


        // otherwise, iterate through all items and give to player
        // drop item to ground if inventory full
        for (ItemStack item : items) {
            if (player.getInventory().firstEmpty() == -1) {
                player.getWorld().dropItem(player.getLocation(), item);
            } else {
                player.getInventory().addItem(item);
                player.updateInventory();
            }
        }
    }

    public static String itemsToString(List<ItemStack> items, String chatColor) {

        if (items == null || items.size() == 0) {
            return "";
        }

        StringBuilder str = new StringBuilder();
        for (int i = 0; i < items.size(); i++) {
            ItemStack itemStack = items.get(i);
            int amount = itemStack.getAmount();

            String name;

            if (itemStack.getItemMeta().hasDisplayName()) {
                name = itemStack.getItemMeta().getDisplayName();
            } else {
                name = itemStack.getType().toString().toLowerCase().replace("_", " ");
            }

            str.append(chatColor);

            str.append(amount).append(" ").append(name);

            if (amount > 1) {
                str.append("s");
            }


            if (items.size() == 1) {
                break;
            }

            if (i == items.size() - 2) { // on 2nd to last iteration, add an "and"
                str.append(" and ");
                str.append(ChatColor.RESET);
            } else if (i != items.size() - 1) { // on the last iteration, don't add another comma
                str.append(ChatColor.RESET);
                str.append(", ");
            }

        }
        return str.toString();
    }

}
