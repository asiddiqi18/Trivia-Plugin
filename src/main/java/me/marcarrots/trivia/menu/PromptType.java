package me.marcarrots.trivia.menu;

import me.marcarrots.trivia.QuestionHolder;
import me.marcarrots.trivia.Trivia;
import me.marcarrots.trivia.menu.subMenus.ListMenu;
import me.marcarrots.trivia.menu.subMenus.ParameterMenu;
import me.marcarrots.trivia.menu.subMenus.RewardsSpecificMenu;
import me.marcarrots.trivia.menu.subMenus.ViewMenu;
import org.bukkit.ChatColor;

public enum PromptType {

    SET_ROUNDS(ChatColor.DARK_AQUA + "Enter the number of rounds you'd like this game to have.", ChatColor.GREEN + "The amount of rounds has been modified.", MenuType.PARAMETER_MENU),
    SET_TIME(ChatColor.DARK_AQUA + "Enter the time for each round.", ChatColor.GREEN + "The time for each round has been modified.", MenuType.PARAMETER_MENU),
    NEW_QUESTION(null, null, MenuType.LIST_MENU),
    EDIT_QUESTION(ChatColor.DARK_AQUA + "Enter the new question you'd like.", ChatColor.GREEN + "This question has been modified.", MenuType.VIEW_MENU),
    EDIT_ANSWER(ChatColor.DARK_AQUA + "Enter the new answer you'd like.", ChatColor.GREEN + "This answer has been been modified.", MenuType.VIEW_MENU),
    SET_MONEY(ChatColor.DARK_AQUA + "Enter the amount of money this winner will receive.", ChatColor.GREEN + "This money reward has been modified.", MenuType.REWARDS_MENU),
    SET_EXPERIENCE(ChatColor.DARK_AQUA + "Enter the amount of experience this winner will receive.", ChatColor.GREEN + "This experience reward has been modified.", MenuType.REWARDS_MENU),
    SET_WIN_MESSAGE(ChatColor.DARK_AQUA + "Enter the message this winner will receive when they win.", ChatColor.GREEN + "This reward message has been modified.", MenuType.REWARDS_MENU),
    DELETE(null, null, null);

    private final String prompt;
    private final String back = " Type" + ChatColor.RED + " 'back' " + ChatColor.DARK_AQUA + "to return.";
    private final String success;
    private final MenuType menu;

    PromptType(String prompt, String success, MenuType menu) {
        this.prompt = prompt;
        this.success = success;
        this.menu = menu;
    }

    public String getPrompt() {
        return prompt + back;
    }

    public String getSuccess() {
        return success;
    }

    public void openNewMenu(PlayerMenuUtility playerMenuUtility, Trivia trivia, QuestionHolder questionHolder, int place) {
        switch (menu) {
            case MAIN_MENU:
                break;
            case LIST_MENU:
                new ListMenu(playerMenuUtility, trivia, questionHolder).open();
                break;
            case VIEW_MENU:
                new ViewMenu(playerMenuUtility, trivia, questionHolder).open();
                break;
            case PARAMETER_MENU:
                new ParameterMenu(playerMenuUtility, trivia, questionHolder).open();
                break;
            case REWARDS_MENU:
                new RewardsSpecificMenu(playerMenuUtility, trivia, questionHolder, place).open();
                break;
        }
    }
}
