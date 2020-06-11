package me.marcarrots.trivia.menu;

import me.marcarrots.trivia.Question;
import me.marcarrots.trivia.QuestionHolder;
import me.marcarrots.trivia.Trivia;
import me.marcarrots.trivia.listeners.ChatEvent;
import me.marcarrots.trivia.menu.subMenus.ListMenu;
import me.marcarrots.trivia.menu.subMenus.ParameterMenu;
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
    private final ChatEvent chatEvent;
    private final Question question;

    public ConversationPrompt(PromptType promptType, PlayerMenuUtility playerMenuUtility, Trivia trivia, QuestionHolder questionHolder, ChatEvent chatEvent) {
        this.promptType = promptType;
        this.playerMenuUtility = playerMenuUtility;
        this.trivia = trivia;
        this.questionHolder = questionHolder;
        this.chatEvent = chatEvent;
        this.question = null;
    }

    public ConversationPrompt(PromptType promptType, PlayerMenuUtility playerMenuUtility, Trivia trivia, QuestionHolder questionHolder, ChatEvent chatEvent, Question question) {
        this.promptType = promptType;
        this.playerMenuUtility = playerMenuUtility;
        this.trivia = trivia;
        this.questionHolder = questionHolder;
        this.chatEvent = chatEvent;
        this.question = question;
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
                        return new ConversationPrompt(PromptType.NEW_QUESTION, playerMenuUtility, trivia, questionHolder, chatEvent, question);
                    } else if (question.getAnswerString() == null) {
                        question.setAnswer(input);
                        List<String> unparsedQuestions = trivia.getConfig().getStringList("Questions and Answers");
                        unparsedQuestions.add(question.getQuestionString() + " /$/ " + question.getAnswerString());
                        trivia.getConfig().set("Questions and Answers", unparsedQuestions);
                        trivia.saveConfig();
                        trivia.parseFiles();
                        player.spigot().sendMessage(new TextComponent(ChatColor.GREEN + "This question has been added to the pool."));
                        new ListMenu(playerMenuUtility, trivia, questionHolder, chatEvent).open();
                        return Prompt.END_OF_CONVERSATION;
                    }
                case EDIT_QUESTION:
                    questionHolder.updateQuestionToFile(trivia, playerMenuUtility.getQuestion(), input, promptType);
                    trivia.parseFiles();
                    player.spigot().sendMessage(new TextComponent(ChatColor.GREEN + "This question has been modified."));
                    playerMenuUtility.setQuestionString(input);
                    new ViewMenu(playerMenuUtility, trivia, questionHolder, chatEvent).open();
                    return Prompt.END_OF_CONVERSATION;
                case EDIT_ANSWER:
                    questionHolder.updateQuestionToFile(trivia, playerMenuUtility.getQuestion(), input, promptType);
                    trivia.parseFiles();
                    player.spigot().sendMessage(new TextComponent(ChatColor.GREEN + "This answer has been been modified."));
                    playerMenuUtility.setAnswerString(input);
                    new ViewMenu(playerMenuUtility, trivia, questionHolder, chatEvent).open();
                    return Prompt.END_OF_CONVERSATION;
            }
        } catch (NumberFormatException e) {
            player.sendMessage("Please enter a valid number.");
        }

        ParameterMenu menu = new ParameterMenu(playerMenuUtility, trivia, questionHolder, chatEvent);
        menu.open();
        return Prompt.END_OF_CONVERSATION;
    }


}