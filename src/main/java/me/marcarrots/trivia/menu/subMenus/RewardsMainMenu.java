/*
 * Trivia by MarCarrot, 2020
 */

package me.marcarrots.trivia.menu.subMenus;

import me.marcarrots.trivia.QuestionHolder;
import me.marcarrots.trivia.Trivia;
import me.marcarrots.trivia.menu.Menu;
import me.marcarrots.trivia.menu.MenuType;
import me.marcarrots.trivia.menu.PlayerMenuUtility;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

public class RewardsMainMenu extends Menu {
    public RewardsMainMenu(PlayerMenuUtility playerMenuUtility, Trivia trivia, QuestionHolder questionHolder) {
        super(playerMenuUtility, trivia, questionHolder);
    }

    @Override
    public String getMenuName() {
        return "Rewards Menu";
    }

    @Override
    public int getSlots() {
        return 36;
    }

    @Override
    public void handleMenuClick(InventoryClickEvent event) {
        cancelEvent(event);

        Material type = event.getCurrentItem().getType();
        Player player = (Player) event.getWhoClicked();

        switch (ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName())) {
            case "First Place":
                new RewardsSpecificMenu(playerMenuUtility, trivia, questionHolder, 1).open();
                break;
            case "Second Place":
                new RewardsSpecificMenu(playerMenuUtility, trivia, questionHolder, 2).open();
                break;
            case "Third Place":
                new RewardsSpecificMenu(playerMenuUtility, trivia, questionHolder, 3).open();
                break;
        }

        if (type == Material.ARROW) {
            new MainMenu(trivia.getPlayerMenuUtility(player), trivia, questionHolder).open();
        } else if (event.getCurrentItem().equals(CLOSE)) {
            player.closeInventory();
        }

    }

    @Override
    public void handleMenuClose(InventoryCloseEvent event) {

    }

    @Override
    public void setMenuItems() {
        insertItem(Material.CHEST, "First Place", 11);

        insertItem(Material.CHEST, "Second Place", 13);

        insertItem(Material.CHEST, "Third Place", 15);

        fillRest();

        inventory.setItem(27, BACK);
        inventory.setItem(31, CLOSE);
    }
}
