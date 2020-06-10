package me.marcarrots.trivia.menu;


import me.marcarrots.trivia.QuestionHolder;
import me.marcarrots.trivia.Trivia;
import me.marcarrots.trivia.listeners.ChatEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;


public abstract class Menu implements InventoryHolder {

    protected Inventory inventory;

    protected PlayerMenuUtility playerMenuUtility;
    protected final Trivia trivia;
    protected final QuestionHolder questionHolder;
    protected final ChatEvent chatEvent;

    protected ItemStack FILLER_GLASS;

    protected ItemStack BACK;

    protected ItemStack CLOSE;

    public Menu(PlayerMenuUtility playerMenuUtility, Trivia trivia, QuestionHolder questionHolder, ChatEvent chatEvent) {

        this.playerMenuUtility = playerMenuUtility;
        this.trivia = trivia;
        this.questionHolder = questionHolder;
        this.chatEvent = chatEvent;
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

    public abstract void handleMenu(InventoryClickEvent event);

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
        for (int i = 0; i < 27; i++) {
            if (inventory.getItem(i) != null) {
                inventory.setItem(13, FILLER_GLASS);
            }
        }
    }

}
