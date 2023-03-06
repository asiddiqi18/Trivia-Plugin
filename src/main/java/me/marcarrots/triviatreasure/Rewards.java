/*
 * Trivia by MarCarrot, 2020
 */

package me.marcarrots.triviatreasure;

import me.marcarrots.triviatreasure.effects.Fireworks;
import me.marcarrots.triviatreasure.language.Lang;
import me.marcarrots.triviatreasure.language.Placeholder;
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

    final TriviaTreasure triviaTreasure;
    final int place; // place = 0 -> per round reward
    int experience;
    double money;


    boolean summonFireworks;
    String message;
    List<ItemStack> items;
    List<String> commands;

    public Rewards(TriviaTreasure triviaTreasure, int place) {
        items = new ArrayList<>();
        this.triviaTreasure = triviaTreasure;
        this.place = place;
        experience = 0;
        getValuesFromRewardsFile();
    }

    public static String itemsToString(List<ItemStack> items) {

        if (items == null || items.size() == 0) {
            return "";
        }

        StringBuilder str = new StringBuilder();
        for (int i = 0; i < items.size(); i++) {
            ItemStack itemStack = items.get(i);
            int amount = itemStack.getAmount();

            String name;

            if (itemStack.getItemMeta() != null && itemStack.getItemMeta().hasDisplayName()) {
                name = itemStack.getItemMeta().getDisplayName();
            } else {
                name = itemStack.getType().toString().toLowerCase().replace("_", " ");
            }

            str.append(ChatColor.RESET).append(amount).append(" ").append(name);

            if (amount > 1 && !name.endsWith("s")) {
                str.append("s");
            }

            if (items.size() == 1) {
                break;
            }

            if (i == items.size() - 2) { // on 2nd to last iteration, add an "and"
                str.append(ChatColor.RESET);
                str.append(" and ");
            } else if (i != items.size() - 1) { // on the last iteration, don't add another comma
                str.append(ChatColor.RESET);
                str.append(", ");
            }

        }
        return str.toString();
    }

    public boolean isSummonFireworks() {
        return summonFireworks;
    }

    public int getExperience() {
        return experience;
    }

    public void setExperience(int experience) {
        this.experience = experience;
        triviaTreasure.getRewardsFile().getData().set(place + ".Experience", experience);
        triviaTreasure.getRewardsFile().saveData();
    }

    public String getMessage() {
        return message != null ? ChatColor.translateAlternateColorCodes('&', message) : null;
    }

    public void setMessage(String message) {
        this.message = message;
        triviaTreasure.getRewardsFile().getData().set(place + ".Message", message);
        triviaTreasure.getRewardsFile().saveData();
    }

    public void flipSummonFireworks() {
        this.summonFireworks = !this.summonFireworks;
        triviaTreasure.getRewardsFile().getData().set(place + ".Summon Fireworks", this.summonFireworks);
        triviaTreasure.getRewardsFile().saveData();
    }

    public double getMoney() {
        return money;
    }

    public void setMoney(double money) {
        if (money < 0) {
            money *= -1;
        }
        this.money = money;
        triviaTreasure.getRewardsFile().getData().set(place + ".Money", money);
        triviaTreasure.getRewardsFile().saveData();
    }

    public List<ItemStack> getItems() {
        return items;
    }

    public void setItems(List<ItemStack> items) {
        this.items = items;
        triviaTreasure.getRewardsFile().getData().set(place + ".Items", items);
    }

    @SuppressWarnings("unchecked")
    private void getValuesFromRewardsFile() {
        money = triviaTreasure.getRewardsFile().getData().getDouble(place + ".Money");
        experience = triviaTreasure.getRewardsFile().getData().getInt(place + ".Experience");
        message = triviaTreasure.getRewardsFile().getData().getString(place + ".Message");
        summonFireworks = triviaTreasure.getRewardsFile().getData().getBoolean(place + ".Summon Fireworks");
        items = (List<ItemStack>) triviaTreasure.getRewardsFile().getData().get(place + ".Items");
        commands = (List<String>) triviaTreasure.getRewardsFile().getData().get(place + ".Commands");
    }

    public void giveReward(Player player) {

        // send money to player if vault is enabled
        if (triviaTreasure.isVaultEnabled() && money > 0) {
            EconomyResponse r = TriviaTreasure.getEcon().depositPlayer(player, getMoney());
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

        if (summonFireworks) {
            Fireworks.summonFireWork(player);
        }

        // send experience to player
        if (experience != 0) {
            player.giveExp(getExperience());
            triviaTreasure.getStats().addExperienceWon(player, experience);
        }

        if (place != 0) {
            triviaTreasure.getStats().addGamesWon(player, place);
        }

        if (triviaTreasure.isVaultEnabled()) {
            triviaTreasure.getStats().addMoneyWon(player, money);
        }

        // send reward message to player if there is one
        if (message != null && !message.equalsIgnoreCase("none")) {
            BukkitScheduler scheduler = getServer().getScheduler();
            String formattedMessage = getMessage();
            int itemsIndex = formattedMessage.indexOf("%items%");
            if (itemsIndex != -1) {
                formattedMessage = formattedMessage.replace("%items%", itemsToString(items));
            }
            String moneyFormatted = NumberFormat.getCurrencyInstance().format(money);
            formattedMessage = formattedMessage.replace("%money%", moneyFormatted);
            String experienceFormatted = NumberFormat.getIntegerInstance().format(experience);
            formattedMessage = formattedMessage.replace("%experience%", experienceFormatted);

            Lang.fillPlaceholders(new Placeholder.PlaceholderBuilder().player(player).build(), formattedMessage);
            String finalFormattedMessage = formattedMessage;
            scheduler.scheduleSyncDelayedTask(triviaTreasure, () -> player.sendMessage(finalFormattedMessage), 3);
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

}
