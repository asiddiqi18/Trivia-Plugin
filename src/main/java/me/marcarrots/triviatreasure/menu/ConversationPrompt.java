package me.marcarrots.triviatreasure.menu;

import me.marcarrots.triviatreasure.TriviaTreasure;
import net.md_5.bungee.api.ChatColor;
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
    private final TriviaTreasure triviaTreasure;

    private int place;

    public ConversationPrompt(TriviaTreasure triviaTreasure, PromptType promptType) {
        this.triviaTreasure = triviaTreasure;
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
                    triviaTreasure.getPlayerMenuUtility(player).setTotalRounds(Integer.parseInt(input));
                    break;
                case SET_TIME:
                    triviaTreasure.getPlayerMenuUtility(player).setTimePer(Integer.parseInt(input));
                    break;
                case NEW_ENTRY_QUESTION:
                    context.setSessionData("new_question", input);
                    return new ConversationPrompt(triviaTreasure, PromptType.NEW_ENTRY_ANSWER);
                case NEW_ENTRY_ANSWER:
                    List<String> answers = Arrays.asList(input.split("\\s*,\\s*"));
                    triviaTreasure.getQuestionHolder().writeQuestions(triviaTreasure, (String) context.getSessionData("new_question"), answers, player.getName());
                case EDIT_QUESTION:
                    triviaTreasure.getQuestionHolder().updateQuestion(triviaTreasure, triviaTreasure.getPlayerMenuUtility(player).getQuestion(), input, promptType);
                    triviaTreasure.getPlayerMenuUtility(player).setQuestionString(input);
                    break;
                case EDIT_ANSWER:
                    triviaTreasure.getQuestionHolder().updateQuestion(triviaTreasure, triviaTreasure.getPlayerMenuUtility(player).getQuestion(), input, promptType);
                    triviaTreasure.getPlayerMenuUtility(player).setAnswerString(Arrays.asList(input.split("\\s*,\\s*")));
                    break;
                case SET_MONEY:
                    triviaTreasure.getRewards()[place].setMoney(Double.parseDouble(input));
                    break;
                case SET_EXPERIENCE:
                    triviaTreasure.getRewards()[place].setExperience(Integer.parseInt(input));
                    break;
                case SET_WIN_MESSAGE:
                    triviaTreasure.getRewards()[place].setMessage(input);
                    break;
            }

            if (promptType.getSuccess() != null) {
                player.spigot().sendMessage(new TextComponent(promptType.getSuccess()));
            }

        } catch (NumberFormatException e) {
            player.spigot().sendMessage(new TextComponent(ChatColor.RED + "Please enter a valid number."));
        }

        promptType.openNewMenu(triviaTreasure, player, place);
        return Prompt.END_OF_CONVERSATION;
    }


}