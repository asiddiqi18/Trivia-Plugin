/*
 * Trivia by MarCarrot, 2020
 */

package me.marcarrots.trivia.menu.subMenus;

import me.marcarrots.trivia.QuestionHolder;
import me.marcarrots.trivia.Rewards;
import me.marcarrots.trivia.Trivia;
import me.marcarrots.trivia.menu.Menu;
import me.marcarrots.trivia.menu.PlayerMenuUtility;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class RewardsSpecificMenu extends Menu {

    private final int place;

    public RewardsSpecificMenu(PlayerMenuUtility playerMenuUtility, Trivia trivia, QuestionHolder questionHolder, int place) {
        super(playerMenuUtility, trivia, questionHolder);
        this.place = place;
    }

    @Override
    public String getMenuName() {
        return "Specific Rewards";
    }

    @Override
    public int getSlots() {
        return 45;
    }

    @Override
    public void handleMenuClick(InventoryClickEvent event) {
        Material type = event.getCurrentItem().getType();
        Player player = (Player) event.getWhoClicked();

        switch (ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName())) {
            case "Rewarded Money":
                player.sendMessage(trivia.getRewards()[0].toString());
                new RewardsSpecificMenu(playerMenuUtility, trivia, questionHolder, 1).open();
                break;
            case "Reward Message":
                player.sendMessage(trivia.getRewards()[1].toString());
                new RewardsSpecificMenu(playerMenuUtility, trivia, questionHolder, 2).open();
                break;
        }

        if (type == Material.ARROW) {
            new RewardsMainMenu(trivia.getPlayerMenuUtility(player), trivia, questionHolder).open();
        } else if (event.getCurrentItem().equals(CLOSE)) {
            player.closeInventory();
        }

    }

    @Override
    public void handleMenuClose(InventoryCloseEvent event) {
        List<ItemStack> items = new ArrayList<>();
        for (int i = 10; i < 36; i++) {

            ItemStack item = event.getView().getTopInventory().getItem(i);

            if (item == null || item.equals(FILLER_GLASS)) {
                continue;
            }

            items.add(item);
            event.getPlayer().sendMessage(item.toString());
        }
        items.forEach(item -> trivia.getConfig().set("Rewards." + place + ".Items", item));
        trivia.getRewards()[place - 1].setItems(items);
        trivia.saveConfig();
    }

    @Override
    public void setMenuItems() {
        Rewards reward = trivia.getRewards()[place - 1]; // place starts at 1, while index starts at 0.

        if (reward.getItems() != null) {
            int index = 0;
            for (int i = 10; i < 36; i++) {
                if (inventory.getItem(i) != null) {
                    continue;
                }
                if (index == reward.getItems().size()) {
                    break;
                }
                inventory.setItem(i, reward.getItems().get(index));
                index++;
            }
        }

        for (int i = 1; i < 8; i++) {
            inventory.setItem(i, FILLER_GLASS);
        }

        for (int i = 0; i <= 27; i += 9) {
            inventory.setItem(i, FILLER_GLASS);
            inventory.setItem(i + 8, FILLER_GLASS);
        }

        for (int i = 37; i < 45; i++) {
            inventory.setItem(i, FILLER_GLASS);
        }

        insertItem(Material.EMERALD, "Rewarded Money", "$" + reward.getMoney(), 38, true);
        inventory.setItem(36, BACK);
        inventory.setItem(40, CLOSE);
    }

}
