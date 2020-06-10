package me.marcarrots.trivia.menu.subMenus;

import me.marcarrots.trivia.Game;
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

import java.util.Arrays;

public class ParameterMenu extends Menu {


    public ParameterMenu(PlayerMenuUtility playerMenuUtility, Trivia trivia, QuestionHolder questionHolder, ChatEvent chatEvent) {
        super(playerMenuUtility, trivia, questionHolder, chatEvent);
    }


    @Override
    public String getMenuName() {
        return "Game parameters";
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
            new Game(trivia, questionHolder, chatEvent);
            player.closeInventory();
        } else if (event.getCurrentItem().equals(CLOSE)) {
            player.closeInventory();
        }
    }

    @Override
    public void setMenuItems() {

        insertItem(Material.OAK_SIGN, "Total rounds", "Current: 10 rounds", 10);

        insertItem(Material.CLOCK, "Time Per Question", "Current: 10 seconds", 12);

        insertItem(Material.CLOCK, "Allow question repetition", "Current: False", 14);

        insertItem(Material.GREEN_TERRACOTTA, "Start Trivia", "", 16);

        fillRest();

    }

    private void insertItem(Material oakSign, String s, String s2, int i) {
        ItemStack amountItem = new ItemStack(oakSign, 1);
        ItemMeta amountMeta = amountItem.getItemMeta();
        amountMeta.setDisplayName(ChatColor.GREEN + s);
        amountMeta.setLore(Arrays.asList(s2, ChatColor.RED + "Click here to change."));
        amountItem.setItemMeta(amountMeta);
        inventory.setItem(i, amountItem);
    }

}
