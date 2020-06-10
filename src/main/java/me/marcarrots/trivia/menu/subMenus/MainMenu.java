package me.marcarrots.trivia.menu.subMenus;

import me.marcarrots.trivia.QuestionHolder;
import me.marcarrots.trivia.Trivia;
import me.marcarrots.trivia.listeners.ChatEvent;
import me.marcarrots.trivia.menu.Menu;
import me.marcarrots.trivia.menu.MenuType;
import me.marcarrots.trivia.menu.PlayerMenuUtility;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class MainMenu extends Menu {


    public MainMenu(PlayerMenuUtility playerMenuUtility, Trivia trivia, QuestionHolder questionHolder, ChatEvent chatEvent) {
        super(playerMenuUtility, trivia, questionHolder, chatEvent);
    }

    @Override
    public String getMenuName() {
        return "Main Menu";
    }

    @Override
    public int getSlots() {
        return 27;
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
            new ParameterMenu(playerMenuUtility, trivia, questionHolder, chatEvent).open();
        } else if (type == Material.PAPER) {
            new ListMenu(playerMenuUtility, trivia, questionHolder, chatEvent).open();
        } else if (event.getCurrentItem().equals(CLOSE)) {
            player.closeInventory();
        }
    }

    @Override
    public void setMenuItems() {

        ItemStack messageItem = new ItemStack(Material.GREEN_TERRACOTTA, 1);
        ItemMeta messageMeta = messageItem.getItemMeta();
        messageMeta.setDisplayName(ChatColor.GREEN + "Start Trivia");
        messageItem.setItemMeta(messageMeta);
        inventory.setItem(10, messageItem);

        ItemStack listItem = new ItemStack(Material.GREEN_TERRACOTTA, 1);
        ItemMeta listMeta = listItem.getItemMeta();
        listMeta.setDisplayName(ChatColor.GREEN + "Start Trivia");
        listItem.setItemMeta(messageMeta);
        inventory.setItem(13, listItem);

        fillRest();

    }
}
