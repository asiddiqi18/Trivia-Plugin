package me.marcarrots.trivia.menu;


import me.marcarrots.trivia.Language.Lang;
import me.marcarrots.trivia.QuestionHolder;
import me.marcarrots.trivia.Trivia;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public abstract class Menu implements InventoryHolder {

    protected final Trivia trivia;
    protected final QuestionHolder questionHolder;
    protected Inventory inventory;
    protected PlayerMenuUtility playerMenuUtility;
    protected ItemStack FILLER_GLASS;

    protected ItemStack BACK;

    protected ItemStack CLOSE;

    public Menu(PlayerMenuUtility playerMenuUtility, Trivia trivia, QuestionHolder questionHolder) {

        this.playerMenuUtility = playerMenuUtility;
        this.trivia = trivia;
        this.questionHolder = questionHolder;
        FILLER_GLASS = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta FILLER_GLASS_META = FILLER_GLASS.getItemMeta();
        FILLER_GLASS_META.setDisplayName(" ");
        FILLER_GLASS.setItemMeta(FILLER_GLASS_META);

        CLOSE = new ItemStack(Material.BARRIER, 1);
        ItemMeta close_meta = CLOSE.getItemMeta();
        close_meta.setDisplayName(Lang.MENU_CLOSE.format_multiple(null)[0]);
        CLOSE.setItemMeta(close_meta);

        BACK = new ItemStack(Material.ARROW, 1);
        ItemMeta back_meta = BACK.getItemMeta();
        back_meta.setDisplayName(Lang.MENU_BACK.format_multiple(null)[0]);
        BACK.setItemMeta(back_meta);
    }

    public abstract String getMenuName();

    public abstract int getSlots();

    public abstract void handleMenuClick(InventoryClickEvent event);

    public abstract void handleMenuClose(InventoryCloseEvent event);

    public abstract void setMenuItems();

    public void open() {
        inventory = Bukkit.createInventory(this, getSlots(), getMenuName());
        this.setMenuItems();

        playerMenuUtility.getOwner().openInventory(inventory);
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    protected void fillRest() {
        for (int i = 0; i < getSlots(); i++) {
            if (inventory.getItem(i) == null) {
                inventory.setItem(i, FILLER_GLASS);
            }
        }
    }

    protected void insertItem(Material material, String s, int i) {
        ItemStack amountItem = new ItemStack(material, 1);
        ItemMeta amountMeta = amountItem.getItemMeta();
        if (amountMeta != null) {
            amountMeta.setDisplayName(ChatColor.GREEN + s);
        }
        amountItem.setItemMeta(amountMeta);
        inventory.setItem(i, amountItem);
    }

    protected void insertItem(Material material, String displayName, List<String> lore, int i, boolean changeable) {
        ItemStack amountItem = new ItemStack(material, 1);
        ItemMeta amountMeta = amountItem.getItemMeta();
        List<String> itemLore = new ArrayList<>(lore);
        if (amountMeta != null) {
            amountMeta.setDisplayName(ChatColor.GREEN + displayName);
            if (changeable) {
                itemLore.addAll(Arrays.asList(Lang.MENU_CHANGE.format_multiple(null)));
            }
            amountMeta.setLore(itemLore);
        }
        amountItem.setItemMeta(amountMeta);
        inventory.setItem(i, amountItem);
    }

    protected void insertItemWrap(Material material, String s, String s2, int i) {
        ItemStack amountItem = new ItemStack(material, 1);
        ItemMeta amountMeta = amountItem.getItemMeta();
        if (amountMeta != null) {
            amountMeta.setDisplayName(ChatColor.GREEN + s);
            List<String> loreList;
            loreList = WordWrapLore(ChatColor.LIGHT_PURPLE + s2, ChatColor.LIGHT_PURPLE, 50);
            loreList.addAll(Arrays.asList(Lang.MENU_CHANGE.format_multiple(null)));
            amountMeta.setLore(loreList);
        }
        amountItem.setItemMeta(amountMeta);
        inventory.setItem(i, amountItem);
    }

    protected List<String> WordWrapLore(String string, ChatColor color, int wrapLength) {
        StringBuilder sb = new StringBuilder(string);
        int i = 0;
        while (i + wrapLength < sb.length() && (i = sb.lastIndexOf(" ", i + wrapLength)) != -1) {
            sb.replace(i, i + 1, "\n" + color);
        }
        return new ArrayList<>(Arrays.asList(sb.toString().split("\n")));

    }

}
