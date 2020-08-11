package me.marcarrots.trivia.menu;

import me.marcarrots.trivia.Question;
import me.marcarrots.trivia.QuestionHolder;
import me.marcarrots.trivia.Trivia;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
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
        if (promptType == PromptType.NEW_QUESTION) {
            if (question.getQuestionString() == null) {
                return ChatColor.DARK_AQUA + "Enter the question you'd like to use. Type" + ChatColor.RED + " 'back' " + ChatColor.DARK_AQUA + "to return.";
            } else if (question.getAnswerString() == null) {
                return ChatColor.DARK_AQUA + "Enter the answer you'd like to use for this question.";
            } else {
                return "An error has occurred";
            }
        } else {
            return promptType.getPrompt();
        }
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
                    Bukkit.getLogger().info("(A)");
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
                        promptType.openNewMenu(playerMenuUtility, trivia, questionHolder, place);
                        return Prompt.END_OF_CONVERSATION;
                    }

                case EDIT_QUESTION:
                    questionHolder.updateQuestionToFile(trivia, playerMenuUtility.getQuestion(), input, promptType);
                    trivia.parseFiles();
                    playerMenuUtility.setQuestionString(input);
                    break;

                case EDIT_ANSWER:
                    questionHolder.updateQuestionToFile(trivia, playerMenuUtility.getQuestion(), input, promptType);
                    trivia.parseFiles();
                    playerMenuUtility.setAnswerString(input);
                    break;

                case SET_MONEY:
                    trivia.getRewards()[place - 1].setMoney(Double.parseDouble(input));
                    break;

                case SET_EXPERIENCE:
                    trivia.getRewards()[place - 1].setExperience(Integer.parseInt(input));
                    break;

                case SET_WIN_MESSAGE:
                    trivia.getRewards()[place - 1].setMessage(input);
                    break;

            }
        } catch (NumberFormatException e) {
            player.spigot().sendMessage(new TextComponent("Please enter a valid number."));
        }
        Bukkit.getLogger().info("(B)");

        if (promptType.getSuccess() != null) {
            player.spigot().sendMessage(new TextComponent(promptType.getSuccess()));
            Bukkit.getLogger().info("(C)");

        }
        Bukkit.getLogger().info("(D)");
        promptType.openNewMenu(playerMenuUtility, trivia, questionHolder, place);
        Bukkit.getLogger().info("(E)");

        return Prompt.END_OF_CONVERSATION;
    }


}