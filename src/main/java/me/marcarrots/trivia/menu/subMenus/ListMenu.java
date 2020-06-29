package me.marcarrots.trivia.menu.subMenus;

import me.marcarrots.trivia.Question;
import me.marcarrots.trivia.QuestionHolder;
import me.marcarrots.trivia.Trivia;
import me.marcarrots.trivia.listeners.ChatEvent;
import me.marcarrots.trivia.listeners.PlayerJoin;
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

import java.util.Collections;
import java.util.List;

public class ListMenu extends PaginatedMenu {


    public ListMenu(PlayerMenuUtility playerMenuUtility, Trivia trivia, QuestionHolder questionHolder) {
        super(playerMenuUtility, trivia, questionHolder);
    }


    @Override
    public String getMenuName() {
        return "List of Trivia Questions";
    }

    @Override
    public int getSlots() {
        return 54;
    }

    @Override
    public void handleMenuClick(InventoryClickEvent event) {
        cancelEvent(event);
        Material type = event.getCurrentItem().getType();
        Player player = (Player) event.getWhoClicked();

        List<Question> questionList = questionHolder.getTriviaQuestionList();
        ConversationFactory conversationFactory = new ConversationFactory(trivia);

        if (type == Material.EMERALD) {
            Question question = new Question();
            Conversation conversation = conversationFactory.withFirstPrompt(new ConversationPrompt(PromptType.NEW_QUESTION
                    , playerMenuUtility, trivia, questionHolder, question)).withLocalEcho(false).withTimeout(60).buildConversation(player);
            conversation.begin();
            player.closeInventory();
        } else if (type == Material.PAPER) {
            playerMenuUtility.setPreviousMenu(MenuType.LIST_MENU);
            playerMenuUtility.setQuestion(questionHolder.getQuestion(event.getCurrentItem().getItemMeta().getPersistentDataContainer().get(new NamespacedKey(trivia, "trivia_question"), PersistentDataType.STRING)));
            new ViewMenu(playerMenuUtility, trivia, questionHolder).open();
        } else if (event.getCurrentItem().equals(CLOSE)) {
            player.closeInventory();
        } else if (type == Material.DARK_OAK_BUTTON) {
            if (ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName()).equalsIgnoreCase("Left")) {
                if (page == 0) {
                    player.sendMessage(ChatColor.GRAY + "You are already on the first page.");
                } else {
                    page = page - 1;
                    super.open();
                }
            } else if (ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName()).equalsIgnoreCase(
                    "Right")) {
                if (!((index + 1) >= questionList.size())) {
                    page = page + 1;
                    super.open();
                } else {
                    player.sendMessage(ChatColor.GRAY + "You are already on the last page.");
                }
            }
        } else if (type == Material.ARROW) {
            playerMenuUtility.setPreviousMenu(MenuType.LIST_MENU);
            new MainMenu(playerMenuUtility, trivia, questionHolder).open();
        } else {
            event.setCancelled(true);
        }

    }

    @Override
    public void handleMenuClose(InventoryCloseEvent event) {

    }

    @Override
    public void setMenuItems() {

        addMenuBorder();
        List<Question> questionList = questionHolder.getTriviaQuestionList();

        if (!questionList.isEmpty()) {
            for (int i = 0; i < getMaxItemsPerPage(); i++) {
                index = getMaxItemsPerPage() * page + i;
                if (index >= questionList.size()) {
                    break;
                }
                if (questionList.get(index) != null) {
                    Question question = questionList.get(index);

                    ItemStack questionItem = new ItemStack(Material.PAPER, 1);
                    ItemMeta questionMeta = questionItem.getItemMeta();
                    questionMeta.setDisplayName(ChatColor.DARK_AQUA + String.valueOf(question.getQuestionString()));
                    questionMeta.setLore(WordWrapLore(ChatColor.GREEN + String.valueOf(question.getAnswerString())));

                    questionMeta.getPersistentDataContainer().set(new NamespacedKey(trivia,
                            "trivia_question"), PersistentDataType.STRING, question.getQuestionString());

                    questionItem.setItemMeta(questionMeta);

                    insertItem(Material.EMERALD, "(+) Add new Trivia Question", 53);

                    inventory.addItem(questionItem);

                }
            }
        }

        if (playerMenuUtility.getPreviousMenu() != null) {
            ItemStack previousMenuItem = BACK;
            ItemMeta previousMenuMeta = previousMenuItem.getItemMeta();
            if (playerMenuUtility.getPreviousMenu() == MenuType.MAIN_MENU) {
                previousMenuMeta.setLore(Collections.singletonList("§7... to main menu."));
            }
            previousMenuItem.setItemMeta(previousMenuMeta);
            inventory.setItem(45, previousMenuItem);
        }

    }
}
