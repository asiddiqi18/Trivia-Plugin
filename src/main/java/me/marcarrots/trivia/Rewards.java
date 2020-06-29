/*
 * Trivia by MarCarrot, 2020
 */

package me.marcarrots.trivia;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Rewards {

    Trivia trivia;
    int place;
    double money;
    String message;
    List<ItemStack> items = new ArrayList<>();

    public String getMessage() {
        if (message == null) {
            return "No message is set.";
        }
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
        trivia.getConfig().set("Rewards." + place + ".Message", message);
    }

    public double getMoney() {
        return money;
    }

    public void setMoney(double money) {
        this.money = money;
        trivia.getConfig().set("Rewards." + place + ".Money", money);

    }

    public List<ItemStack> getItems() {
        return items;
    }

    public void setItems(List<ItemStack> items) {
        this.items = items;
        trivia.getConfig().set("Rewards." + place + ".Items", items);
    }

    public Rewards(Trivia trivia, int place) {
        this.trivia = trivia;
        this.place = place + 1;
        getValues();
    }

    private void getValues() {
        money = trivia.getConfig().getDouble("Rewards." + place + ".Money");
        items = (List<ItemStack>) trivia.getConfig().get("Rewards." + place + ".Items");
        if (items == null || items.size() == 0) {
            return;
        }
        for (ItemStack item : items) {
            Bukkit.getLogger().info(item.toString());
        }
    }

    @Override
    public String toString() {
        return items == null ? "Money: " + money : "Money: " + money + ", Items: " + items;
    }

}
