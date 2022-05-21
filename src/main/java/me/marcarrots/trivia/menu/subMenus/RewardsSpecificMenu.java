/*
 * Trivia by MarCarrot, 2020
 */

package me.marcarrots.trivia.menu.subMenus;

import me.marcarrots.trivia.Rewards;
import me.marcarrots.trivia.Trivia;
import me.marcarrots.trivia.language.Lang;
import me.marcarrots.trivia.menu.ConversationPrompt;
import me.marcarrots.trivia.menu.Menu;
import me.marcarrots.trivia.menu.PromptType;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;

public class RewardsSpecificMenu extends Menu {

    private final int place;

    public RewardsSpecificMenu(Trivia trivia, Player player, int place) {
        super(trivia, player);
        this.place = place;
    }

    @Override
    public String getMenuName() {
        return place != 0 ? "Reward for Placing at " + place : "Reward for Answering First Per Round";
    }

    @Override
    public int getSlots() {
        return 45;
    }

    @Override
    public void handleMenuClick(InventoryClickEvent event) {
        Material type = event.getCurrentItem().getType();
        Player player = (Player) event.getWhoClicked();

        ConversationFactory conversationFactory = new ConversationFactory(trivia);
        Conversation conversation;
        switch (event.getSlot()) {
            case 38: // change money
                event.setCancelled(true);
                conversation = conversationFactory.withFirstPrompt(new ConversationPrompt(PromptType.SET_MONEY, player, trivia).setPlace(place)).withLocalEcho(false).withTimeout(60).buildConversation(player);
                conversation.begin();
                player.closeInventory();
                break;
            case 42: // change experience
                event.setCancelled(true);
                conversation = conversationFactory.withFirstPrompt(new ConversationPrompt(PromptType.SET_EXPERIENCE, player, trivia).setPlace(place)).withLocalEcho(false).withTimeout(60).buildConversation(player);
                conversation.begin();
                player.closeInventory();
                break;
            case 43: // set fireworks
                event.setCancelled(true);
                Rewards reward = trivia.getRewards()[place];
                reward.flipSummonFireworks();
                placeFireworks(reward);
                break;
            case 44: // change win message
                event.setCancelled(true);
                conversation = conversationFactory.withFirstPrompt(new ConversationPrompt(PromptType.SET_WIN_MESSAGE, player, trivia).setPlace(place)).withLocalEcho(false).withTimeout(60).buildConversation(player);
                conversation.begin();
                player.closeInventory();
                break;
        }

        if (event.getCurrentItem().equals(FILLER_GLASS)) {
            event.setCancelled(true);
        } else if (type == Material.ARROW) {
            event.setCancelled(true);
            new RewardsMainMenu(trivia, player).open();
        } else if (event.getCurrentItem().equals(CLOSE)) {
            event.setCancelled(true);
            player.closeInventory();
        }

    }

    @Override
    public void handleMenuClose(InventoryCloseEvent event) {
        List<ItemStack> items = new ArrayList<>();
        Inventory topInventory = event.getView().getTopInventory();
        for (int i = 10; i < 36; i++) {
            ItemStack item = topInventory.getItem(i);
            if (item == null || item.equals(FILLER_GLASS)) {
                continue;
            }
            items.add(item);
        }

        trivia.getLogger().log(Level.INFO, String.format("%s has modified the rewards settings for place %d.", event.getPlayer().getName(), place));
        trivia.getRewards()[place].setItems(items);
        trivia.getRewardsFile().saveData();
    }

    @Override
    public void setMenuItems() {

        // get rewards at nth place
        Rewards reward = null;
        try {
            reward = trivia.getRewards()[place];
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (reward == null) {
            return;
        }

        // populate inventory with rewarded items
        if (reward.getItems() != null) {
            int index = 0;
            for (int i = 10; i < 36; i++) {
                if (inventory.getItem(i) != null) {
                    continue;
                }
                if (index == reward.getItems().size()) {
                    break;
                }
                inventory.setItem(i, reward.getItems().get(index));
                index++;
            }
        }

        // add filler glass to edges
        for (int i = 1; i <= 7; i++) {
            inventory.setItem(i, FILLER_GLASS);
            inventory.setItem(36 + i, FILLER_GLASS);
        }
        for (int i = 0; i <= 36; i += 9) {
            inventory.setItem(i, FILLER_GLASS);
            inventory.setItem(i + 8, FILLER_GLASS);
        }

        if (trivia.isVaultEnabled()) {
            insertItem(38, Material.EMERALD, Lang.REWARDS_SPECIFIC_MONEY.format_single(), "$" + reward.getMoney(), true, false);
        } else {
            insertItem(38, Material.EMERALD, ChatColor.DARK_RED + "Rewarded Money", ChatColor.RED + "Vault required to give money rewards!", false, false);
        }


        insertItem(42, Material.EXPERIENCE_BOTTLE, Lang.REWARDS_SPECIFIC_EXP.format_single(), String.valueOf(reward.getExperience()), true, false);
        placeFireworks(reward);
        insertItem(44, Material.WRITABLE_BOOK, Lang.REWARDS_SPECIFIC_MESSAGE.format_single(), Arrays.asList(ChatColor.DARK_PURPLE + "The message below will be shown to this winner.", ChatColor.STRIKETHROUGH + "--------------------", ChatColor.WHITE + "Placeholders:", ChatColor.WHITE + "%items% - writes out a list of items won", ChatColor.WHITE + "%money% - writes out the money reward", ChatColor.WHITE + "%experience% - writes out the experience reward", ChatColor.STRIKETHROUGH + "--------------------", reward.getMessage() != null ? ChatColor.GREEN + reward.getMessage() : ChatColor.DARK_PURPLE + "No message is set."), true, true);
        inventory.setItem(36, BACK);
        inventory.setItem(40, CLOSE);
    }

    private void placeFireworks(Rewards reward) {
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.DARK_PURPLE + "Set to true to spawn fireworks on this winning player after the game.");
        if (reward.isSummonFireworks()) {
            lore.add(ChatColor.DARK_PURPLE + "Current: " + ChatColor.DARK_GREEN + "True");
            insertItem(43, Material.FIREWORK_ROCKET, Lang.REWARDS_SPECIFIC_FIREWORKS.format_single(), lore, true, true);
        } else {
            lore.add(ChatColor.DARK_PURPLE + "Current: " + ChatColor.DARK_RED + "False");
            insertItem(43, Material.FIREWORK_ROCKET, Lang.REWARDS_SPECIFIC_FIREWORKS.format_single(), lore, true, true);
        }
    }

}
