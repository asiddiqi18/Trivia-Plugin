package me.marcarrots.trivia.menu.subMenus;

import me.marcarrots.trivia.Question;
import me.marcarrots.trivia.QuestionHolder;
import me.marcarrots.trivia.Trivia;
import me.marcarrots.trivia.menu.*;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class ListMenu extends PaginatedMenu {
    public ListMenu(PlayerMenuUtility playerMenuUtility, Trivia trivia, QuestionHolder questionHolder) {
        super(playerMenuUtility, trivia, questionHolder);
    }

    public String getMenuName() {
        return "List of Trivia Questions";
    }

    public int getSlots() {
        return 54;
    }

    public void handleMenuClick(InventoryClickEvent event) {
        event.setCancelled(true);

        Material type = Objects.requireNonNull(event.getCurrentItem()).getType();
        Player player = (Player) event.getWhoClicked();

        List<Question> questionList = questionHolder.getTriviaQuestionList();
        ConversationFactory conversationFactory = new ConversationFactory(trivia);
        if (type == Material.EMERALD) {
            Question question = new Question();
            Conversation conversation = conversationFactory.withFirstPrompt(new ConversationPrompt(PromptType.NEW_QUESTION, playerMenuUtility, trivia, questionHolder, question)).withLocalEcho(false).withTimeout(60).buildConversation(player);
            conversation.begin();
            player.closeInventory();
        } else if (type == Material.PAPER) {
            playerMenuUtility.setPreviousMenu(MenuType.LIST_MENU);
            playerMenuUtility.setQuestion(questionHolder.getQuestion(event.getCurrentItem().getItemMeta().getPersistentDataContainer().get(new NamespacedKey(trivia, "trivia_question_id"), PersistentDataType.INTEGER).intValue()));
            (new ViewMenu(playerMenuUtility, trivia, questionHolder)).open();
        } else if (event.getCurrentItem().equals(CLOSE)) {
            player.closeInventory();
        } else if (type == Material.DARK_OAK_BUTTON) {
            if (ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName()).equalsIgnoreCase("Left")) {
                if (page == 0) {
                    player.sendMessage(ChatColor.GRAY + "You are already on the first page.");
                } else {
                    page--;
                    open();
                }
            } else if (ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName()).equalsIgnoreCase("Right")) {
                if (index + 1 < questionList.size()) {
                    page++;
                    open();
                } else {
                    player.sendMessage(ChatColor.GRAY + "You are already on the last page.");
                }
            }
        } else if (type == Material.ARROW) {
            playerMenuUtility.setPreviousMenu(MenuType.LIST_MENU);
            (new MainMenu(playerMenuUtility, trivia, questionHolder)).open();
        }
    }

    public void handleMenuClose(InventoryCloseEvent event) {
    }

    public void setMenuItems() {
        addMenuBorder();
        List<Question> questionList = questionHolder.getTriviaQuestionList();
        if (!questionList.isEmpty())
            for (int i = 0; i < getMaxItemsPerPage(); i++) {
                index = getMaxItemsPerPage() * page + i;
                if (index >= questionList.size())
                    break;
                if (questionList.get(index) != null) {
                    Question question = questionList.get(index);
                    ItemStack questionItem = new ItemStack(Material.PAPER, 1);
                    ItemMeta questionMeta = questionItem.getItemMeta();
                    questionMeta.setDisplayName(ChatColor.AQUA + "Question #" + question.getId());

                    List<String> questionWrap = WordWrapLore("Question: " + ChatColor.YELLOW + question.getQuestionString(), ChatColor.YELLOW);
                    List<String> answerWrap = WordWrapLore("Answer: " + ChatColor.GREEN + String.valueOf(question.getAnswerList()), ChatColor.GREEN);
                    List<String> loreList = new ArrayList<>(questionWrap);
                    loreList.addAll(answerWrap);
                    questionMeta.setLore(loreList);

                    questionMeta.getPersistentDataContainer().set(new NamespacedKey(trivia, "trivia_question_id"), PersistentDataType.INTEGER,
                            Integer.valueOf(question.getId()));
                    questionItem.setItemMeta(questionMeta);
                    inventory.addItem(questionItem);
                }
            }
        insertItem(Material.EMERALD, "(+) Add new Trivia Question", 53);
        if (playerMenuUtility.getPreviousMenu() != null) {
            ItemStack previousMenuItem = BACK;
            ItemMeta previousMenuMeta = previousMenuItem.getItemMeta();
            if (playerMenuUtility.getPreviousMenu() == MenuType.MAIN_MENU)
                previousMenuMeta.setLore(Collections.singletonList("to main menu."));
            previousMenuItem.setItemMeta(previousMenuMeta);
            inventory.setItem(45, previousMenuItem);
        }
    }
}
