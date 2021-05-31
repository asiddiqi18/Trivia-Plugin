package me.marcarrots.trivia.menu.subMenus;

import me.marcarrots.trivia.Language.Lang;
import me.marcarrots.trivia.QuestionHolder;
import me.marcarrots.trivia.Timer;
import me.marcarrots.trivia.Trivia;
import me.marcarrots.trivia.menu.Menu;
import me.marcarrots.trivia.menu.MenuType;
import me.marcarrots.trivia.menu.PlayerMenuUtility;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.Arrays;
import java.util.Objects;

public class MainMenu extends Menu {


    public MainMenu(PlayerMenuUtility playerMenuUtility, Trivia trivia, QuestionHolder questionHolder) {
        super(playerMenuUtility, trivia, questionHolder);
    }

    @Override
    public String getMenuName() {
        return Lang.MAIN_MENU_TITLE.format(null);
    }

    @Override
    public int getSlots() {
        return 36;
    }

    @Override
    public void handleMenuClick(InventoryClickEvent event) {
        event.setCancelled(true);

        Material type = Objects.requireNonNull(event.getCurrentItem()).getType();
        Player player = (Player) event.getWhoClicked();

        playerMenuUtility.setPreviousMenu(MenuType.MAIN_MENU);
        if (type == Material.GREEN_TERRACOTTA) {
            new ParameterMenu(playerMenuUtility, trivia, questionHolder).open();
        } else if (type == Material.PAPER) {
            new ListMenu(playerMenuUtility, trivia, questionHolder).open();
        } else if (type == Material.EMERALD) {
            new RewardsMainMenu(playerMenuUtility, trivia, questionHolder).open();
        } else if (event.getCurrentItem().equals(CLOSE)) {
            player.closeInventory();
        }
    }

    @Override
    public void handleMenuClose(InventoryCloseEvent event) {

    }

    @Override
    public void setMenuItems() {

        BukkitScheduler scheduler = trivia.getServer().getScheduler();

        insertItem(Material.GREEN_TERRACOTTA, Lang.MAIN_MENU_START.format(null), WordWrapLore(ChatColor.DARK_PURPLE + Lang.MAIN_MENU_START_DESCRIPTION.format(null), ChatColor.DARK_PURPLE, 30), 11, false);

        insertItem(Material.EMERALD, Lang.MAIN_MENU_REWARDS.format(null), WordWrapLore(ChatColor.DARK_PURPLE + Lang.MAIN_MENU_REWARDS_DESCRIPTION.format(null), ChatColor.DARK_PURPLE, 30), 13, false);

        insertItem(Material.PAPER, Lang.MAIN_MENU_LIST.format(null), WordWrapLore(ChatColor.DARK_PURPLE + Lang.MAIN_MENU_LIST_DESCRIPTION.format(null), ChatColor.DARK_PURPLE, 30), 15, false);


        if (trivia.getAutomatedGameManager().isSchedulingEnabled()) {
            scheduler.scheduleSyncRepeatingTask(trivia, () -> insertItem(Material.CLOCK, "Time Until Next Scheduled Game", Arrays.asList(ChatColor.YELLOW + Timer.getElapsedTime(trivia.getAutomatedGameManager().getNextAutomatedTime()), ChatColor.LIGHT_PURPLE + "Minimum players needed: " + trivia.getAutomatedGameManager().getAutomatedPlayerReq()), 35, false), 0, 20);
        } else {
            scheduler.scheduleSyncRepeatingTask(trivia, () -> insertItem(Material.CLOCK, ChatColor.RED + "Scheduled Games Not Enabled", Arrays.asList(ChatColor.YELLOW + "Enable this feature through the " + ChatColor.UNDERLINE + "config.yml", ChatColor.YELLOW + "in order to automatically start games!"), 35, false), 0, 20);
        }

        fillRest();

        inventory.setItem(31, CLOSE);


    }


}
