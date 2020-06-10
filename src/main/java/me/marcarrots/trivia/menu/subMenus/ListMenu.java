package me.marcarrots.trivia.menu.subMenus;

import me.marcarrots.trivia.QuestionHolder;
import me.marcarrots.trivia.Trivia;
import me.marcarrots.trivia.listeners.ChatEvent;
import me.marcarrots.trivia.menu.Menu;
import me.marcarrots.trivia.menu.PlayerMenuUtility;
import org.bukkit.event.inventory.InventoryClickEvent;

public class ListMenu extends Menu {


    public ListMenu(PlayerMenuUtility playerMenuUtility, Trivia trivia, QuestionHolder questionHolder, ChatEvent chatEvent) {
        super(playerMenuUtility, trivia, questionHolder, chatEvent);
    }


    @Override
    public String getMenuName() {
        return null;
    }

    @Override
    public int getSlots() {
        return 0;
    }

    @Override
    public void handleMenu(InventoryClickEvent event) {

    }

    @Override
    public void setMenuItems() {

    }
}
