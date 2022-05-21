package me.marcarrots.trivia.menu.subMenus;

import me.marcarrots.trivia.Trivia;
import me.marcarrots.trivia.language.Lang;
import me.marcarrots.trivia.language.Placeholder;
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

public class ViewMenu extends Menu {
    public ViewMenu(Trivia trivia, Player player) {
        super(trivia, player);
    }

    @Override

    public String getMenuName() {
        return Lang.VIEW_MENU_TITLE.format_single(new Placeholder.PlaceholderBuilder()
                .val(String.valueOf(trivia.getPlayerMenuUtility(player).getQuestion().getId()))
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
        ConversationFactory conversationFactory = new ConversationFactory(trivia);

        if (type == Material.GREEN_TERRACOTTA) {
            Conversation conversation = conversationFactory.withFirstPrompt(new ConversationPrompt(PromptType.EDIT_QUESTION
                    , player, trivia)).withLocalEcho(false).withTimeout(60).buildConversation(player);
            conversation.begin();
            player.closeInventory();
        } else if (type == Material.YELLOW_TERRACOTTA) {
            Conversation conversation = conversationFactory.withFirstPrompt(new ConversationPrompt(PromptType.EDIT_ANSWER
                    , player, trivia)).withLocalEcho(false).withTimeout(60).buildConversation(player);
            conversation.begin();
            player.closeInventory();
        } else if (type == Material.RED_TERRACOTTA) {
            trivia.getQuestionHolder().updateQuestionToFile(trivia, trivia.getPlayerMenuUtility(player).getQuestion(), null, PromptType.DELETE);
            player.sendMessage(ChatColor.GREEN + "This trivia question has been been removed.");
            new ListMenu(trivia, player).open();
        } else if (type == Material.ARROW) {
            new ListMenu(trivia, player).open();
        } else if (event.getCurrentItem().equals(CLOSE)) {
            player.closeInventory();
        }

    }

    @Override
    public void handleMenuClose(InventoryCloseEvent event) {

    }

    @Override
    public void setMenuItems() {

        insertItem(11, Material.GREEN_TERRACOTTA, Lang.VIEW_MENU_QUESTION.format_single(), ChatColor.DARK_PURPLE + trivia.getPlayerMenuUtility(player).getQuestion().getQuestionString(), true, true);
        insertItem(13, Material.YELLOW_TERRACOTTA, Lang.VIEW_MENU_ANSWER.format_single(), ChatColor.DARK_PURPLE + trivia.getPlayerMenuUtility(player).getQuestion().getAnswerList().toString(), true, true);
        insertItem(15, Material.RED_TERRACOTTA, Lang.VIEW_MENU_DELETE.format_single(), "", false, false);

        inventory.setItem(27, BACK);
        inventory.setItem(31, CLOSE);
        fillRest();
    }
}
