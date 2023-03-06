package me.marcarrots.triviatreasure.menu;

import me.marcarrots.triviatreasure.TriviaTreasure;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public abstract class PaginatedMenu extends Menu {

    protected final int maxItemsPerPage = 28;
    protected int page = 0;
    protected int index = 0;

    public PaginatedMenu(TriviaTreasure triviaTreasure, Player player) {
        super(triviaTreasure, player);
    }

    //Set the border and menu buttons for the menu
    public void addMenuBorder() {
        ItemStack left = new ItemStack(Material.DARK_OAK_BUTTON, 1);
        ItemMeta leftMeta = left.getItemMeta();
        leftMeta.setDisplayName(ChatColor.GREEN + "Previous Page");
        left.setItemMeta(leftMeta);

        inventory.setItem(48, left);

        inventory.setItem(49, CLOSE);

        ItemStack right = new ItemStack(Material.DARK_OAK_BUTTON, 1);
        ItemMeta rightmeta = right.getItemMeta();
        rightmeta.setDisplayName(ChatColor.GREEN + "Next Page");
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
