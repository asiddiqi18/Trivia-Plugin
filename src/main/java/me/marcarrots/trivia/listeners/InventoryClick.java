package me.marcarrots.trivia.listeners;

import me.marcarrots.trivia.menu.Menu;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.InventoryHolder;

public class InventoryClick implements Listener {

    @EventHandler
    public void onMenuClick(InventoryClickEvent event) {

        if (event.getClickedInventory() == null || event.getCurrentItem() == null) {
            return;
        }

        InventoryHolder holder = event.getClickedInventory().getHolder();

        if (holder instanceof Menu) {

            if (event.isShiftClick()) {
                event.setCancelled(true);
            }

            Menu menu = (Menu) holder;
            menu.handleMenuClick(event);

        }

    }

    @EventHandler
    public void onMenuClose(InventoryCloseEvent event) {

        InventoryHolder holder = event.getInventory().getHolder();

        if (holder instanceof Menu) {

            Menu menu = (Menu) holder;
            menu.handleMenuClose(event);

        }

    }

}
