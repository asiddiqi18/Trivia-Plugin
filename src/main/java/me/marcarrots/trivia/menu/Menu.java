package me.marcarrots.trivia.menu;


import me.marcarrots.trivia.QuestionHolder;
import me.marcarrots.trivia.Trivia;
import me.marcarrots.trivia.listeners.ChatEvent;
import me.marcarrots.trivia.listeners.PlayerJoin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;
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
        close_meta.setDisplayName(ChatColor.RED + "Close");
        CLOSE.setItemMeta(close_meta);

        BACK = new ItemStack(Material.ARROW, 1);
        ItemMeta back_meta = BACK.getItemMeta();
        back_meta.setDisplayName(ChatColor.GREEN + "Go back");
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

    protected void insertItem(Material material, String displayName, String lore, int i, boolean changeable) {
        ItemStack amountItem = new ItemStack(material, 1);
        ItemMeta amountMeta = amountItem.getItemMeta();
        if (amountMeta != null) {
            amountMeta.setDisplayName(ChatColor.GREEN + displayName);
            List<String> loreList = new ArrayList<>(3);
            loreList.add(lore);
            if (changeable) {
                loreList.add(ChatColor.RED + "Click here to change.");
            }
            amountMeta.setLore(loreList);
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
            loreList = WordWrapLore(ChatColor.LIGHT_PURPLE + s2);
            loreList.add(ChatColor.RED + "Click here to change.");
            amountMeta.setLore(loreList);
        }
        amountItem.setItemMeta(amountMeta);
        inventory.setItem(i, amountItem);
    }

    protected List<String> WordWrapLore(String string) {
        StringBuilder sb = new StringBuilder(string);
        int wordWrapLength = 50;
        int i = 0;
        while (i + wordWrapLength < sb.length() && (i = sb.lastIndexOf(" ", i + wordWrapLength)) != -1) {
            sb.replace(i, i + 1, "\n§d");
        }
        return new ArrayList<>(Arrays.asList(sb.toString().split("\n")));

    }

    protected void cancelEvent(InventoryClickEvent event) {
        if (event.getClick() == ClickType.SHIFT_LEFT || event.getClick() == ClickType.DOUBLE_CLICK || event.getClick() == ClickType.DROP) {
            event.setCancelled(true);
        }

        event.setCancelled(true);
    }

}
