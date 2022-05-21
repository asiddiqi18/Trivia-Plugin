/*
 * Trivia by MarCarrot, 2020
 */

package me.marcarrots.trivia.menu.subMenus;

import me.marcarrots.trivia.Trivia;
import me.marcarrots.trivia.language.Lang;
import me.marcarrots.trivia.menu.Menu;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

public class RewardsMainMenu extends Menu {
    public RewardsMainMenu(Trivia trivia, Player player) {
        super(trivia, player);
    }

    @Override
    public String getMenuName() {
        return Lang.REWARDS_GENERAL_MENU_TITLE.format_single();
    }

    @Override
    public int getSlots() {
        return 36;
    }

    @Override
    public void handleMenuClick(InventoryClickEvent event) {
        event.setCancelled(true);

        Material type = event.getCurrentItem().getType();
        Player player = (Player) event.getWhoClicked();

        switch (event.getSlot()) {
            case 11:
                new RewardsSpecificMenu(trivia, player, 1).open();
                break;
            case 13:
                new RewardsSpecificMenu(trivia, player, 2).open();
                break;
            case 15:
                new RewardsSpecificMenu(trivia, player, 3).open();
                break;
            case 35:
                new RewardsSpecificMenu(trivia, player, 0).open();
                break;
        }

        if (type == Material.ARROW) {
            new MainMenu(trivia, player).open();
        } else if (event.getCurrentItem().equals(CLOSE)) {
            player.closeInventory();
        }

    }

    @Override
    public void handleMenuClose(InventoryCloseEvent event) {

    }

    @Override
    public void setMenuItems() {

        String lore = "View and modify rewards given to the %s place winner at the end of a trivia game.";

        insertItem(11, Material.CHEST, Lang.REWARDS_GENERAL_FIRST.format_single(), String.format(ChatColor.DARK_PURPLE + lore, "first"), true, true);

        insertItem(13, Material.CHEST, Lang.REWARDS_GENERAL_SECOND.format_single(), String.format(ChatColor.DARK_PURPLE + lore, "second"), true, true);

        insertItem(15, Material.CHEST, Lang.REWARDS_GENERAL_THIRD.format_single(), String.format(ChatColor.DARK_PURPLE + lore, "third"), true, true);

        insertItem(35, Material.ENDER_CHEST, ChatColor.LIGHT_PURPLE + "Per-Round Reward", ChatColor.DARK_PURPLE + "View and modify rewards given per round basis to the first answerer.", true, true);

        fillRest();

        inventory.setItem(27, BACK);
        inventory.setItem(31, CLOSE);
    }
}
