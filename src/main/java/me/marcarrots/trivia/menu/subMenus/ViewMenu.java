package me.marcarrots.trivia.menu.subMenus;

import me.marcarrots.trivia.QuestionHolder;
import me.marcarrots.trivia.Trivia;
import me.marcarrots.trivia.listeners.ChatEvent;
import me.marcarrots.trivia.listeners.PlayerJoin;
import me.marcarrots.trivia.menu.ConversationPrompt;
import me.marcarrots.trivia.menu.Menu;
import me.marcarrots.trivia.menu.PlayerMenuUtility;
import me.marcarrots.trivia.menu.PromptType;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

public class ViewMenu extends Menu {
    public ViewMenu(PlayerMenuUtility playerMenuUtility, Trivia trivia, QuestionHolder questionHolder) {
        super(playerMenuUtility, trivia, questionHolder);
    }

    @Override
    public String getMenuName() {
        return "Trivia Question";
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
        ConversationFactory conversationFactory = new ConversationFactory(trivia);

        if (type == Material.GREEN_TERRACOTTA) {
            Conversation conversation = conversationFactory.withFirstPrompt(new ConversationPrompt(PromptType.EDIT_QUESTION
                    , playerMenuUtility, trivia, questionHolder)).withLocalEcho(false).withTimeout(60).buildConversation(player);
            conversation.begin();
        } else if (type == Material.YELLOW_TERRACOTTA) {
            Conversation conversation = conversationFactory.withFirstPrompt(new ConversationPrompt(PromptType.EDIT_ANSWER
                    , playerMenuUtility, trivia, questionHolder)).withLocalEcho(false).withTimeout(60).buildConversation(player);
            conversation.begin();
        } else if (type == Material.RED_TERRACOTTA) {
            questionHolder.updateQuestionToFile(trivia, playerMenuUtility.getQuestion(), null, PromptType.DELETE);
            trivia.parseFiles();
            player.sendMessage(ChatColor.GREEN + "This trivia question has been been removed.");
            new ListMenu(playerMenuUtility, trivia, questionHolder).open();
            return;
        } else if (type == Material.ARROW) {
            new ListMenu(trivia.getPlayerMenuUtility(player), trivia, questionHolder).open();
            return;
        } else if (event.getCurrentItem().equals(FILLER_GLASS)) {
            return;
        }
        player.closeInventory();
    }

    @Override
    public void handleMenuClose(InventoryCloseEvent event) {

    }

    @Override
    public void setMenuItems() {
        insertItemWrap(Material.GREEN_TERRACOTTA, "Question", playerMenuUtility.getQuestion().getQuestionString(), 11);
        insertItemWrap(Material.YELLOW_TERRACOTTA, "Answer", playerMenuUtility.getQuestion().getAnswerString(), 13);
        insertItem(Material.RED_TERRACOTTA, "Delete This Question", 15);
        inventory.setItem(27, BACK);
        inventory.setItem(31, CLOSE);
        fillRest();
    }
}
