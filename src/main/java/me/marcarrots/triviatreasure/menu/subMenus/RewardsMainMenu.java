/*
 * Trivia by MarCarrot, 2020
 */

package me.marcarrots.triviatreasure.menu.subMenus;

import me.marcarrots.triviatreasure.TriviaTreasure;
import me.marcarrots.triviatreasure.language.Lang;
import me.marcarrots.triviatreasure.menu.Menu;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

import java.text.MessageFormat;

public class RewardsMainMenu extends Menu {
    public RewardsMainMenu(TriviaTreasure triviaTreasure, Player player) {
        super(triviaTreasure, player);
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

        if (event.getCurrentItem() == null) {
            return;
        }

        Material type = event.getCurrentItem().getType();
        Player player = (Player) event.getWhoClicked();

        switch (event.getSlot()) {
            case 11:
                new RewardsSpecificMenu(triviaTreasure, player, 1).open();
                break;
            case 13:
                new RewardsSpecificMenu(triviaTreasure, player, 2).open();
                break;
            case 15:
                new RewardsSpecificMenu(triviaTreasure, player, 3).open();
                break;
            case 35:
                new RewardsSpecificMenu(triviaTreasure, player, 0).open();
                break;
        }

        if (type == Material.ARROW) {
            new MainMenu(triviaTreasure, player).open();
        } else if (event.getCurrentItem().equals(CLOSE)) {
            player.closeInventory();
        }

    }

    @Override
    public void handleMenuClose(InventoryCloseEvent event) {

    }

    @Override
    public void setMenuItems() {

        String lore = "{0}View and modify rewards given to the {1}{2}{0} place winner at the end of a trivia game.";

        insertItem(11, Material.CHEST, Lang.REWARDS_GENERAL_FIRST.format_single(), MessageFormat.format(lore, ChatColor.DARK_PURPLE, ChatColor.LIGHT_PURPLE, "first"), true, true);

        insertItem(13, Material.CHEST, Lang.REWARDS_GENERAL_SECOND.format_single(),  MessageFormat.format(lore, ChatColor.DARK_PURPLE, ChatColor.LIGHT_PURPLE, "second"), true, true);

        insertItem(15, Material.CHEST, Lang.REWARDS_GENERAL_THIRD.format_single(),  MessageFormat.format(lore, ChatColor.DARK_PURPLE, ChatColor.LIGHT_PURPLE, "third"), true, true);

        insertItem(35, Material.ENDER_CHEST, ChatColor.LIGHT_PURPLE + "Per-Round Reward", ChatColor.DARK_PURPLE + "View and modify rewards given per round basis to the first answerer.", true, true);

        fillRest();

        inventory.setItem(27, BACK);
        inventory.setItem(31, CLOSE);
    }
}
