/*
 * Trivia by MarCarrot, 2020
 */

package me.marcarrots.trivia;

import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class Rewards {

    Trivia trivia;
    int place;
    int experience;
    double money;
    String message;
    List<ItemStack> items;

    public Rewards(Trivia trivia, int place) {
        items = new ArrayList<>();
        this.trivia = trivia;
        this.place = place + 1;
        experience = 0;
        getValuesFromConfig();
    }

    public int getExperience() {
        return experience;
    }

    public void setExperience(int experience) {
        this.experience = experience;
        trivia.getConfig().set("Rewards." + place + ".Experience", experience);
        trivia.saveConfig();
    }

    public String getMessage() {
        return message != null ? ChatColor.translateAlternateColorCodes('&', message) : null;
    }

    public void setMessage(String message) {
        this.message = message;
        trivia.getConfig().set("Rewards." + place + ".Message", message);
        trivia.saveConfig();
    }

    public double getMoney() {
        return money;
    }

    public void setMoney(double money) {
        if (money < 0) {
            money *= -1;
        }
        this.money = money;
        trivia.getConfig().set("Rewards." + place + ".Money", money);
        trivia.saveConfig();
    }

    public List<ItemStack> getItems() {
        return items;
    }

    public void setItems(List<ItemStack> items) {
        this.items = items;
        trivia.getConfig().set("Rewards." + place + ".Items", items);
    }

    private void getValuesFromConfig() {
        money = trivia.getConfig().getDouble("Rewards." + place + ".Money");
        experience = trivia.getConfig().getInt("Rewards." + place + ".Experience");
        message = trivia.getConfig().getString("Rewards." + place + ".Message");
        items = (List<ItemStack>) trivia.getConfig().get("Rewards." + place + ".Items");
    }

}
