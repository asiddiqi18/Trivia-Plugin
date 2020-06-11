package me.marcarrots.trivia.menu.subMenus;

import me.marcarrots.trivia.Game;
import me.marcarrots.trivia.QuestionHolder;
import me.marcarrots.trivia.Trivia;
import me.marcarrots.trivia.listeners.ChatEvent;
import me.marcarrots.trivia.menu.*;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

public class ParameterMenu extends Menu {


    public ParameterMenu(PlayerMenuUtility playerMenuUtility, Trivia trivia, QuestionHolder questionHolder, ChatEvent chatEvent) {
        super(playerMenuUtility, trivia, questionHolder, chatEvent);
    }


    @Override
    public String getMenuName() {
        return "Game parameters";
    }

    @Override
    public int getSlots() {
        return 36;
    }

    @Override
    public void handleMenu(InventoryClickEvent event) {
        if (event.getCurrentItem() == null) {
            return;
        }
        Material type = event.getCurrentItem().getType();
        Player player = (Player) event.getWhoClicked();
        ConversationFactory conversationFactory = new ConversationFactory(trivia);

        playerMenuUtility.setPreviousMenu(MenuType.MAIN_MENU);
        if (type == Material.GREEN_TERRACOTTA) {
            if (trivia.isGameActive()) {
                player.sendMessage(ChatColor.RED + "A trivia game is already in progress.");
                return;
            }
            new Game(trivia, questionHolder, chatEvent, playerMenuUtility).start();
        } else if (type == Material.OAK_SIGN) {
            Conversation conversation = conversationFactory.withFirstPrompt(new ConversationPrompt(PromptType.SET_ROUNDS
                    , playerMenuUtility, trivia, questionHolder, chatEvent)).withLocalEcho(false).withTimeout(60).buildConversation(player);
            conversation.begin();
        } else if (type == Material.CLOCK) {
            Conversation conversation = conversationFactory.withFirstPrompt(new ConversationPrompt(PromptType.SET_TIME
                    , playerMenuUtility, trivia, questionHolder, chatEvent)).withLocalEcho(false).withTimeout(60).buildConversation(player);
            conversation.begin();
        } else if (type == Material.RED_DYE || type == Material.LIME_DYE) {
            playerMenuUtility.setRepeatEnabled();
            setMenuItems();
            return;
        } else if (type == Material.ARROW) {
            new MainMenu(trivia.getPlayerMenuUtility(player), trivia, questionHolder, chatEvent).open();
            return;
        } else if (event.getCurrentItem().equals(FILLER_GLASS)) {
            return;
        }
        player.closeInventory();

    }

    @Override
    public void setMenuItems() {

        insertItem(Material.OAK_SIGN, "Total rounds", ChatColor.DARK_PURPLE + "Current: " + ChatColor.LIGHT_PURPLE + playerMenuUtility.getTotalRounds(), 10);

        insertItem(Material.CLOCK, "Time Per Question", ChatColor.DARK_PURPLE + "Current: " + ChatColor.LIGHT_PURPLE + playerMenuUtility.getTimePer(), 12);


        if (playerMenuUtility.isRepeatEnabled()) {
            insertItem(Material.LIME_DYE, "Allow question repetition", ChatColor.DARK_PURPLE + "Current: " + ChatColor.DARK_GREEN + "True", 14);
        } else {
            insertItem(Material.RED_DYE, "Allow question repetition", ChatColor.DARK_PURPLE + "Current: " + ChatColor.DARK_RED + "False", 14);
        }

        insertItem(Material.GREEN_TERRACOTTA, "Start Trivia", 16);
        inventory.setItem(27, BACK);
        inventory.setItem(31, CLOSE);

        fillRest();

    }

}
