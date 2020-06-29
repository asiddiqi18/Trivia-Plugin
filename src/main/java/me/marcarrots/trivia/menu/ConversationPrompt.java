package me.marcarrots.trivia.menu;

import me.marcarrots.trivia.Question;
import me.marcarrots.trivia.QuestionHolder;
import me.marcarrots.trivia.Trivia;
import me.marcarrots.trivia.listeners.ChatEvent;
import me.marcarrots.trivia.listeners.PlayerJoin;
import me.marcarrots.trivia.menu.subMenus.ListMenu;
import me.marcarrots.trivia.menu.subMenus.ParameterMenu;
import me.marcarrots.trivia.menu.subMenus.RewardsSpecificMenu;
import me.marcarrots.trivia.menu.subMenus.ViewMenu;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;

import java.util.List;

public class ConversationPrompt extends StringPrompt {

    private final PromptType promptType;
    private final PlayerMenuUtility playerMenuUtility;
    private final Trivia trivia;
    private final QuestionHolder questionHolder;
    private final Question question;

    private int place;

    public ConversationPrompt(PromptType promptType, PlayerMenuUtility playerMenuUtility, Trivia trivia, QuestionHolder questionHolder) {
        this.promptType = promptType;
        this.playerMenuUtility = playerMenuUtility;
        this.trivia = trivia;
        this.questionHolder = questionHolder;
        this.question = null;
    }

    public ConversationPrompt(PromptType promptType, PlayerMenuUtility playerMenuUtility, Trivia trivia, QuestionHolder questionHolder, Question question) {
        this.promptType = promptType;
        this.playerMenuUtility = playerMenuUtility;
        this.trivia = trivia;
        this.questionHolder = questionHolder;
        this.question = question;
    }

    public ConversationPrompt setPlace(int place) {
        this.place = place;
        return this;
    }

    @Override
    public String getPromptText(ConversationContext context) {
        switch (promptType) {
            case SET_ROUNDS:
                return ChatColor.DARK_AQUA + "Enter the number of rounds you'd like this game to have. Type" + ChatColor.RED + " 'back' " + ChatColor.DARK_AQUA + "to return.";
            case SET_TIME:
                return ChatColor.DARK_AQUA + "Enter the time for each round. Type" + ChatColor.RED + " 'back' " + ChatColor.DARK_AQUA + "to return.";
            case NEW_QUESTION:
                if (question.getQuestionString() == null) {
                    return ChatColor.DARK_AQUA + "Enter the question you'd like to use. Type" + ChatColor.RED + " 'back' " + ChatColor.DARK_AQUA + "to return.";
                } else if (question.getAnswerString() == null) {
                    return ChatColor.DARK_AQUA + "Enter the answer you'd like to use for this question.";
                }
                break;
            case EDIT_QUESTION:
                return ChatColor.DARK_AQUA + "Enter the new question you'd like. Type" + ChatColor.RED + " 'back' " + ChatColor.DARK_AQUA + "to return.";
            case EDIT_ANSWER:
                return ChatColor.DARK_AQUA + "Enter the new answer you'd like. Type" + ChatColor.RED + " 'back' " + ChatColor.DARK_AQUA + "to return.";
            case DELETE:
                break;
            case SET_MONEY:
                return ChatColor.DARK_AQUA + "Enter the amount of money this winner will receive. Type" + ChatColor.RED + " 'back' " + ChatColor.DARK_AQUA + "to return.";
            case SET_WIN_MESSAGE:
                return ChatColor.DARK_AQUA + "Enter the message this winner will receive when they win. Type" + ChatColor.RED + " 'back' " + ChatColor.DARK_AQUA + "to return.";
            default:
                throw new IllegalStateException("Unexpected value: " + promptType);
        }
        return "Error";
    }

    @Override
    public Prompt acceptInput(ConversationContext context, String input) {

        Player player = ((Player) context.getForWhom()).getPlayer();

        if (input == null || input.equalsIgnoreCase("back") || player == null) {
            context.getForWhom().sendRawMessage("§cYou have exited the prompt.");
            return Prompt.END_OF_CONVERSATION;
        }


        try {
            switch (promptType) {
                case SET_ROUNDS:
                    playerMenuUtility.setTotalRounds(Integer.parseInt(input));
                    break;
                case SET_TIME:
                    playerMenuUtility.setTimePer(Integer.parseInt(input));
                    break;
                case NEW_QUESTION:
                    if (question == null) {
                        return Prompt.END_OF_CONVERSATION;
                    }

                    if (question.getQuestionString() == null) {
                        question.setQuestion(input);
                        return new ConversationPrompt(PromptType.NEW_QUESTION, playerMenuUtility, trivia, questionHolder, question);
                    } else if (question.getAnswerString() == null) {
                        question.setAnswer(input);
                        List<String> unparsedQuestions = trivia.getConfig().getStringList("Questions and Answers");
                        unparsedQuestions.add(question.getQuestionString() + " /$/ " + question.getAnswerString());
                        trivia.getConfig().set("Questions and Answers", unparsedQuestions);
                        trivia.saveConfig();
                        trivia.parseFiles();
                        player.spigot().sendMessage(new TextComponent(ChatColor.GREEN + "This question has been added to the pool."));
                        new ListMenu(playerMenuUtility, trivia, questionHolder).open();
                        return Prompt.END_OF_CONVERSATION;
                    }
                case EDIT_QUESTION:
                    questionHolder.updateQuestionToFile(trivia, playerMenuUtility.getQuestion(), input, promptType);
                    trivia.parseFiles();
                    player.spigot().sendMessage(new TextComponent(ChatColor.GREEN + "This question has been modified."));
                    playerMenuUtility.setQuestionString(input);
                    new ViewMenu(playerMenuUtility, trivia, questionHolder).open();
                    return Prompt.END_OF_CONVERSATION;
                case EDIT_ANSWER:
                    questionHolder.updateQuestionToFile(trivia, playerMenuUtility.getQuestion(), input, promptType);
                    trivia.parseFiles();
                    player.spigot().sendMessage(new TextComponent(ChatColor.GREEN + "This answer has been been modified."));
                    playerMenuUtility.setAnswerString(input);
                    new ViewMenu(playerMenuUtility, trivia, questionHolder).open();
                    return Prompt.END_OF_CONVERSATION;
                case SET_MONEY:
                    trivia.getRewards()[place - 1].setMoney(Double.parseDouble(input));
                    player.spigot().sendMessage(new TextComponent(ChatColor.GREEN + "This money reward has been modified."));
                    new RewardsSpecificMenu(playerMenuUtility, trivia, questionHolder, place).open();
                    return Prompt.END_OF_CONVERSATION;
                case SET_WIN_MESSAGE:
                    trivia.getRewards()[place - 1].setMessage(input);
                    player.spigot().sendMessage(new TextComponent(ChatColor.GREEN + "This reward message has been modified."));
                    new RewardsSpecificMenu(playerMenuUtility, trivia, questionHolder, place).open();
                    return Prompt.END_OF_CONVERSATION;
                case DELETE:
                    break;
            }
        } catch (NumberFormatException e) {
            player.spigot().sendMessage(new TextComponent("Please enter a valid number."));
        }

        if (promptType == PromptType.SET_MONEY) {
            new RewardsSpecificMenu(playerMenuUtility, trivia, questionHolder, place).open();
        } else {
            new ParameterMenu(playerMenuUtility, trivia, questionHolder).open();
        }
        return Prompt.END_OF_CONVERSATION;
    }


}