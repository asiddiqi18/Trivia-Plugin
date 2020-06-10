package me.marcarrots.trivia.listeners;

import me.marcarrots.trivia.menu.Menu;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.InventoryHolder;

public class InventoryClick implements Listener {

    @EventHandler
    public void onMenuClick(InventoryClickEvent event) {

        if (event.getClickedInventory() == null) {
            return;
        }

        InventoryHolder holder = event.getClickedInventory().getHolder();

        if (holder instanceof Menu) {

            if (event.getClick() == ClickType.SHIFT_LEFT || event.getClick() == ClickType.DOUBLE_CLICK || event.getClick() == ClickType.DROP) {
                event.setCancelled(true);
            }

            event.setCancelled(true);

            Menu menu = (Menu) holder;
            menu.handleMenu(event);

        }

    }

}
