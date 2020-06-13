package me.marcarrots.trivia.menu.subMenus;

import me.marcarrots.trivia.QuestionHolder;
import me.marcarrots.trivia.Trivia;
import me.marcarrots.trivia.listeners.ChatEvent;
import me.marcarrots.trivia.listeners.PlayerJoin;
import me.marcarrots.trivia.menu.Menu;
import me.marcarrots.trivia.menu.MenuType;
import me.marcarrots.trivia.menu.PlayerMenuUtility;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

public class MainMenu extends Menu {


    public MainMenu(PlayerMenuUtility playerMenuUtility, Trivia trivia, QuestionHolder questionHolder, ChatEvent chatEvent, PlayerJoin playerJoin) {
        super(playerMenuUtility, trivia, questionHolder, chatEvent, playerJoin);
    }

    @Override
    public String getMenuName() {
        return "Main Menu";
    }

    @Override
    public int getSlots() {
        return 36;
    }

    @Override
    public void handleMenu(InventoryClickEvent event) {
        if (event.getCurrentItem() == null) {
            return;
        }
        Material type = event.getCurrentItem().getType();
        Player player = (Player) event.getWhoClicked();

        playerMenuUtility.setPreviousMenu(MenuType.MAIN_MENU);
        if (type == Material.GREEN_TERRACOTTA) {
            new ParameterMenu(playerMenuUtility, trivia, questionHolder, chatEvent, playerJoin).open();
        } else if (type == Material.PAPER) {
            new ListMenu(playerMenuUtility, trivia, questionHolder, chatEvent, playerJoin).open();
        } else if (event.getCurrentItem().equals(CLOSE)) {
            player.closeInventory();
        }
    }

    @Override
    public void setMenuItems() {

        insertItem(Material.GREEN_TERRACOTTA, "Start Trivia", 11);

        insertItem(Material.PAPER, "List Questions", 15);

        fillRest();

        inventory.setItem(31, CLOSE);


    }
}
