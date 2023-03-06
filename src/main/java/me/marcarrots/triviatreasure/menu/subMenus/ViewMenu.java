package me.marcarrots.triviatreasure.menu.subMenus;

import me.marcarrots.triviatreasure.TriviaTreasure;
import me.marcarrots.triviatreasure.language.Lang;
import me.marcarrots.triviatreasure.language.Placeholder;
import me.marcarrots.triviatreasure.menu.ConversationPrompt;
import me.marcarrots.triviatreasure.menu.Menu;
import me.marcarrots.triviatreasure.menu.PromptType;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

public class ViewMenu extends Menu {
    public ViewMenu(TriviaTreasure triviaTreasure, Player player) {
        super(triviaTreasure, player);
    }

    @Override

    public String getMenuName() {
        return Lang.VIEW_MENU_TITLE.format_single(new Placeholder.PlaceholderBuilder()
                .val(String.valueOf(triviaTreasure.getPlayerMenuUtility(player).getQuestion().getId()))
                .build()
        );
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
        ConversationFactory conversationFactory = new ConversationFactory(triviaTreasure);

        if (type == Material.GREEN_TERRACOTTA) {
            Conversation conversation = conversationFactory.withFirstPrompt(new ConversationPrompt(triviaTreasure, PromptType.EDIT_QUESTION
            )).withLocalEcho(false).withTimeout(60).buildConversation(player);
            conversation.begin();
            player.closeInventory();
        } else if (type == Material.YELLOW_TERRACOTTA) {
            Conversation conversation = conversationFactory.withFirstPrompt(new ConversationPrompt(triviaTreasure, PromptType.EDIT_ANSWER
            )).withLocalEcho(false).withTimeout(60).buildConversation(player);
            conversation.begin();
            player.closeInventory();
        } else if (type == Material.RED_TERRACOTTA) {
            triviaTreasure.getQuestionHolder().updateQuestion(triviaTreasure, triviaTreasure.getPlayerMenuUtility(player).getQuestion(), null, PromptType.DELETE);
            player.sendMessage(ChatColor.GREEN + "This trivia question has been been removed.");
            new ListMenu(triviaTreasure, player).open();
        } else if (type == Material.ARROW) {
            new ListMenu(triviaTreasure, player).open();
        } else if (event.getCurrentItem().equals(CLOSE)) {
            player.closeInventory();
        }

    }

    @Override
    public void handleMenuClose(InventoryCloseEvent event) {

    }

    @Override
    public void setMenuItems() {

        insertItem(11, Material.GREEN_TERRACOTTA, Lang.VIEW_MENU_QUESTION.format_single(), ChatColor.DARK_PURPLE + triviaTreasure.getPlayerMenuUtility(player).getQuestion().getQuestionString(), true, true);
        insertItem(13, Material.YELLOW_TERRACOTTA, Lang.VIEW_MENU_ANSWER.format_single(), ChatColor.DARK_PURPLE + triviaTreasure.getPlayerMenuUtility(player).getQuestion().getAnswerList().toString(), true, true);
        insertItem(15, Material.RED_TERRACOTTA, Lang.VIEW_MENU_DELETE.format_single(), "", false, false);

        inventory.setItem(27, BACK);
        inventory.setItem(31, CLOSE);
        fillRest();
    }
}
