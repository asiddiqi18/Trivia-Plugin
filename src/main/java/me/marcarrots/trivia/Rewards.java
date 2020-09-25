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
    }

}
