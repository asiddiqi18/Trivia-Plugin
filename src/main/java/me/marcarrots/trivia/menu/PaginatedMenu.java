package me.marcarrots.trivia.menu;

import me.marcarrots.trivia.QuestionHolder;
import me.marcarrots.trivia.Trivia;
import me.marcarrots.trivia.listeners.ChatEvent;
import me.marcarrots.trivia.listeners.PlayerJoin;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public abstract class PaginatedMenu extends Menu {

    protected int page = 0;
    protected int maxItemsPerPage = 28;
    protected int index = 0;

    public PaginatedMenu(PlayerMenuUtility playerMenuUtility, Trivia trivia, QuestionHolder questionHolder) {
        super(playerMenuUtility, trivia, questionHolder);

    }

    //Set the border and menu buttons for the menu
    public void addMenuBorder() {
        ItemStack left = new ItemStack(Material.DARK_OAK_BUTTON, 1);
        ItemMeta leftmeta = left.getItemMeta();
        leftmeta.setDisplayName(ChatColor.GREEN + "Left");
        left.setItemMeta(leftmeta);

        inventory.setItem(48, left);

        inventory.setItem(49, CLOSE);

        ItemStack right = new ItemStack(Material.DARK_OAK_BUTTON, 1);
        ItemMeta rightmeta = right.getItemMeta();
        rightmeta.setDisplayName(ChatColor.GREEN + "Right");
        right.setItemMeta(rightmeta);

        inventory.setItem(50, right);

        for (int i = 0; i < 10; i++) {
            if (inventory.getItem(i) == null) {
                inventory.setItem(i, super.FILLER_GLASS);
            }
        }

        inventory.setItem(17, super.FILLER_GLASS);
        inventory.setItem(18, super.FILLER_GLASS);
        inventory.setItem(26, super.FILLER_GLASS);
        inventory.setItem(27, super.FILLER_GLASS);
        inventory.setItem(35, super.FILLER_GLASS);
        inventory.setItem(36, super.FILLER_GLASS);

        for (int i = 44; i < 54; i++) {
            if (inventory.getItem(i) == null) {
                inventory.setItem(i, super.FILLER_GLASS);
            }
        }
    }

    public int getMaxItemsPerPage() {
        return maxItemsPerPage;
    }


}
