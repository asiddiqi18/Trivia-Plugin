package me.marcarrots.trivia.menu.subMenus;

import me.marcarrots.trivia.Game;
import me.marcarrots.trivia.Trivia;
import me.marcarrots.trivia.language.Lang;
import me.marcarrots.trivia.menu.*;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

import java.util.Arrays;

public class ParameterMenu extends Menu {


    public ParameterMenu(PlayerMenuUtility playerMenuUtility, Trivia trivia) {
        super(playerMenuUtility, trivia);
    }


    @Override
    public String getMenuName() {
        return Lang.PARAMS_MENU_TITLE.format_single();
    }

    @Override
    public int getSlots() {
        return 36;
    }

    @Override
    public void handleMenuClick(InventoryClickEvent event) {
        event.setCancelled(true);

        Material type = event.getCurrentItem().getType();
        Player player = (Player) event.getWhoClicked();

        ConversationFactory conversationFactory = new ConversationFactory(trivia);
        playerMenuUtility.setPreviousMenu(MenuType.MAIN_MENU);
        if (type == Material.GREEN_TERRACOTTA) {
            if (trivia.getGame() != null) {
                player.sendMessage(ChatColor.RED + "A trivia game is already in progress.");
                return;
            }
            try {
                long timePerQuestion = playerMenuUtility.getTimePer();
                int amountOfRounds = playerMenuUtility.getTotalRounds();
                boolean doRepetition = playerMenuUtility.isRepeatEnabled();
                CommandSender commandSender = playerMenuUtility.getOwner();
                Game game = new Game(trivia, timePerQuestion, amountOfRounds, doRepetition, commandSender);
                trivia.setGame(game);
                trivia.getGame().start();
            } catch (IllegalAccessException e) {
                player.sendMessage(ChatColor.RED + e.getMessage());
            }
            player.closeInventory();
        } else if (type == Material.OAK_SIGN) {
            Conversation conversation = conversationFactory.withFirstPrompt(new ConversationPrompt(PromptType.SET_ROUNDS
                    , playerMenuUtility, trivia)).withLocalEcho(false).withTimeout(60).buildConversation(player);
            conversation.begin();
            player.closeInventory();
        } else if (type == Material.CLOCK) {
            Conversation conversation = conversationFactory.withFirstPrompt(new ConversationPrompt(PromptType.SET_TIME
                    , playerMenuUtility, trivia)).withLocalEcho(false).withTimeout(60).buildConversation(player);
            conversation.begin();
            player.closeInventory();
        } else if (type == Material.RED_DYE || type == Material.LIME_DYE) {
            playerMenuUtility.setRepeatEnabled();
            setMenuItems();
        } else if (type == Material.ARROW) {
            new MainMenu(trivia.getPlayerMenuUtility(player), trivia).open();
        } else if (event.getCurrentItem().equals(CLOSE)) {
            player.closeInventory();
        }


    }

    @Override
    public void handleMenuClose(InventoryCloseEvent event) {

    }

    @Override
    public void setMenuItems() {

        insertItem(10, Material.OAK_SIGN,
                Lang.PARAMS_MENU_TOTAL.format_single(),
                ChatColor.DARK_PURPLE + "Current: " + ChatColor.LIGHT_PURPLE + playerMenuUtility.getTotalRounds() + " rounds.",
                true, false);

        insertItem(12, Material.CLOCK,
                Lang.PARAMS_MENU_TIME.format_single(),
                ChatColor.DARK_PURPLE + "Current: " + ChatColor.LIGHT_PURPLE + playerMenuUtility.getTimePer() + " seconds.",
                true, false);


        if (playerMenuUtility.isRepeatEnabled()) {
            insertItem(14, Material.LIME_DYE,
                    Lang.PARAMS_MENU_REPEAT.format_single(),
                    ChatColor.DARK_PURPLE + "Current: " + ChatColor.DARK_GREEN + "True",
                    true, false);
        } else {
            insertItem(14, Material.RED_DYE,
                    Lang.PARAMS_MENU_REPEAT.format_single(),
                    ChatColor.DARK_PURPLE + "Current: " + ChatColor.DARK_RED + "False",
                    true, false);
        }

        insertItem(16, Material.GREEN_TERRACOTTA,
                Lang.PARAMS_MENU_START.format_single(),
                Arrays.asList(ChatColor.YELLOW + "" + ChatColor.STRIKETHROUGH + "--------------------", ChatColor.DARK_PURPLE + "Rounds: " + ChatColor.LIGHT_PURPLE + playerMenuUtility.getTotalRounds(), ChatColor.DARK_PURPLE + "Seconds per round: " + ChatColor.LIGHT_PURPLE + playerMenuUtility.getTimePer(), ChatColor.DARK_PURPLE + "Repeat questions: " + ChatColor.LIGHT_PURPLE + playerMenuUtility.isRepeatEnabled()),
                false, false);
        inventory.setItem(27, BACK);
        inventory.setItem(31, CLOSE);
        fillRest();

    }

}
