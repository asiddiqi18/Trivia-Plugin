package me.marcarrots.trivia.menu.subMenus;

import me.marcarrots.trivia.QuestionHolder;
import me.marcarrots.trivia.Trivia;
import me.marcarrots.trivia.menu.Menu;
import me.marcarrots.trivia.menu.MenuType;
import me.marcarrots.trivia.menu.PlayerMenuUtility;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.Arrays;
import java.util.Collections;

public class MainMenu extends Menu {


    public MainMenu(PlayerMenuUtility playerMenuUtility, Trivia trivia, QuestionHolder questionHolder) {
        super(playerMenuUtility, trivia, questionHolder);
    }

    @Override
    public String getMenuName() {
        return "Main Menu";
    }

    @Override
    public int getSlots() {
        return 36;
    }

    @Override
    public void handleMenuClick(InventoryClickEvent event) {
        cancelEvent(event);
        Material type = event.getCurrentItem().getType();
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

        insertItem(Material.GREEN_TERRACOTTA, "Start Trivia", 11);

        insertItem(Material.EMERALD, "Rewards", Collections.singletonList("Adjust trivia prizes that are given to winners."), 13, false);

        insertItem(Material.PAPER, "List Questions", Collections.singletonList("Create new questions or modify existing questions."), 15, false);

        if (trivia.isSchedulingEnabled()) {
            scheduler.scheduleSyncRepeatingTask(trivia, () -> {
                insertItem(Material.CLOCK, "Time Until Next Scheduled Game", Arrays.asList(ChatColor.YELLOW + trivia.getTimeUntilScheduled(), ChatColor.LIGHT_PURPLE + "Minimum players needed: " + trivia.getAutomatedPlayerReq()), 35, false);
            }, 0, 20);
        }

        fillRest();

        inventory.setItem(31, CLOSE);


    }

    
}
