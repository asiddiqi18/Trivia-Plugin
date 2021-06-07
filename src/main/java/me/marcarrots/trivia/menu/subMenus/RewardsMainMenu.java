/*
 * Trivia by MarCarrot, 2020
 */

package me.marcarrots.trivia.menu.subMenus;

import me.marcarrots.trivia.language.Lang;
import me.marcarrots.trivia.QuestionHolder;
import me.marcarrots.trivia.Trivia;
import me.marcarrots.trivia.menu.Menu;
import me.marcarrots.trivia.menu.PlayerMenuUtility;
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
        return Lang.REWARDS_GENERAL_MENU_TITLE.format_single(null);
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
                new RewardsSpecificMenu(playerMenuUtility, trivia, questionHolder, 1).open();
                break;
            case 13:
                new RewardsSpecificMenu(playerMenuUtility, trivia, questionHolder, 2).open();
                break;
            case 15:
                new RewardsSpecificMenu(playerMenuUtility, trivia, questionHolder, 3).open();
                break;
            case 35:
                new RewardsSpecificMenu(playerMenuUtility, trivia, questionHolder, 0).open();
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

        String lore = "View and modify rewards given to the %s place winner at the end of a trivia game.";

        insertItemWrap(Material.CHEST, Lang.REWARDS_GENERAL_FIRST.format_single(null), String.format(lore, "first"), 11);

        insertItemWrap(Material.CHEST, Lang.REWARDS_GENERAL_SECOND.format_single(null), String.format(lore, "second"), 13);

        insertItemWrap(Material.CHEST, Lang.REWARDS_GENERAL_THIRD.format_single(null), String.format(lore, "third"), 15);

        insertItemWrap(Material.ENDER_CHEST, "Per-Round Reward", "View and modify rewards given per round basis to the first answerer.", 35);


        fillRest();

        inventory.setItem(27, BACK);
        inventory.setItem(31, CLOSE);
    }
}
