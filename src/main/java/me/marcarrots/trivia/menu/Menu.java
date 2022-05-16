package me.marcarrots.trivia.menu;


import me.marcarrots.trivia.Trivia;
import me.marcarrots.trivia.language.Lang;
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
import java.util.Collections;
import java.util.List;


public abstract class Menu implements InventoryHolder {

    protected final Trivia trivia;
    protected Inventory inventory;
    protected final PlayerMenuUtility playerMenuUtility;
    protected final ItemStack FILLER_GLASS;

    protected final ItemStack BACK;

    protected final ItemStack CLOSE;

    public Menu(PlayerMenuUtility playerMenuUtility, Trivia trivia) {

        this.playerMenuUtility = playerMenuUtility;
        this.trivia = trivia;
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

    protected void insertItem(int index, Material material, String displayName, String lore, boolean changeable, boolean wrap) {
        ItemStack item = new ItemStack(material, 1);
        List<String> loreList = new ArrayList<>(Collections.singletonList(lore)); // do this because "lore" might not be mutable
        if (lore.equals("")) {
            insertItem(index, item, displayName, null, changeable, wrap);
        } else {
            insertItem(index, item, displayName, loreList, changeable, wrap);
        }
    }

    protected void insertItem(int index, Material material, String displayName, List<String> lore, boolean changeable, boolean wrap) {
        ItemStack item = new ItemStack(material, 1);
        List<String> loreList = new ArrayList<>(lore); // do this because "lore" might not be mutable
        insertItem(index, item, displayName, loreList, changeable, wrap);
    }

    protected void insertItem(int index, ItemStack item, String displayName, List<String> lore, boolean changeable, boolean wrap) {
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(displayName);
            if (lore != null) {
                if (wrap) {
                    List<String> newLoreList = new ArrayList<>();
                    for (String oldLoreLine : lore) {
                        String color = ChatColor.getLastColors(oldLoreLine);
                        newLoreList.addAll(WordWrapLore(oldLoreLine, color, 40));
                    }
                    lore = newLoreList;
                }
                if (changeable) {
                    String[] changeableArray = Lang.MENU_CHANGE.format_multiple(null);
                    List<String> changeableList = Arrays.asList(changeableArray);
                    lore.addAll(changeableList);
                }
                meta.setLore(lore);
            }
        }
        item.setItemMeta(meta);
        if (index == -1) {
            inventory.addItem(item);
        } else {
            inventory.setItem(index, item);
        }
    }

    @SuppressWarnings("SameParameterValue")
    protected List<String> WordWrapLore(String string, String color, int wrapLength) {
        StringBuilder sb = new StringBuilder(color + string);
        int i = 0;
        while (i + wrapLength < sb.length() && (i = sb.lastIndexOf(" ", i + wrapLength)) != -1) {
            sb.replace(i, i + 1, "\n" + color);
        }
        return new ArrayList<>(Arrays.asList(sb.toString().split("\n")));
    }

}
