package me.marcarrots.trivia.menu;

import me.marcarrots.trivia.Trivia;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

public class ConversationPrompt extends StringPrompt {

    private final PromptType promptType;
    private final Trivia trivia;

    private int place;

    public ConversationPrompt(Trivia trivia, PromptType promptType) {
        this.trivia = trivia;
        this.promptType = promptType;
    }

    public ConversationPrompt setPlace(int place) {
        this.place = place;
        return this;
    }

    @Override
    public @NotNull String getPromptText(@NotNull ConversationContext context) {
        return promptType.getPrompt();
    }

    @Nullable
    @Override
    public Prompt acceptInput(ConversationContext context, String input) {
        Player player = ((Player) context.getForWhom()).getPlayer();
        if (input == null || input.equalsIgnoreCase("back") || player == null) {
            context.getForWhom().sendRawMessage("You have exited the prompt.");
            return Prompt.END_OF_CONVERSATION;
        }
        try {
            switch (promptType) {
                case SET_ROUNDS:
                    trivia.getPlayerMenuUtility(player).setTotalRounds(Integer.parseInt(input));
                    break;
                case SET_TIME:
                    trivia.getPlayerMenuUtility(player).setTimePer(Integer.parseInt(input));
                    break;
                case NEW_ENTRY_QUESTION:
                    context.setSessionData("new_question", input);
                    return new ConversationPrompt(trivia, PromptType.NEW_ENTRY_ANSWER);
                case NEW_ENTRY_ANSWER:
                    List<String> answers = Arrays.asList(input.split("\\s*,\\s*"));
                    trivia.getQuestionHolder().writeQuestions(trivia, (String) context.getSessionData("new_question"), answers, player.getName());
                case EDIT_QUESTION:
                    trivia.getQuestionHolder().updateQuestionToFile(trivia, trivia.getPlayerMenuUtility(player).getQuestion(), input, promptType);
                    trivia.getPlayerMenuUtility(player).setQuestionString(input);
                    break;
                case EDIT_ANSWER:
                    trivia.getQuestionHolder().updateQuestionToFile(trivia, trivia.getPlayerMenuUtility(player).getQuestion(), input, promptType);
                    trivia.getPlayerMenuUtility(player).setAnswerString(Arrays.asList(input.split("\\s*,\\s*")));
                    break;
                case SET_MONEY:
                    trivia.getRewards()[place].setMoney(Double.parseDouble(input));
                    break;
                case SET_EXPERIENCE:
                    trivia.getRewards()[place].setExperience(Integer.parseInt(input));
                    break;
                case SET_WIN_MESSAGE:
                    trivia.getRewards()[place].setMessage(input);
                    break;
            }
        } catch (NumberFormatException e) {
            player.spigot().sendMessage(new TextComponent("Please enter a valid number."));
        }

        if (promptType.getSuccess() != null) {
            player.spigot().sendMessage(new TextComponent(promptType.getSuccess()));
        }
        promptType.openNewMenu(trivia, player, place);
        return Prompt.END_OF_CONVERSATION;
    }


}