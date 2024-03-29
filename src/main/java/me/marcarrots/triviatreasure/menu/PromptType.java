package me.marcarrots.triviatreasure.menu;

import me.marcarrots.triviatreasure.TriviaTreasure;
import me.marcarrots.triviatreasure.menu.subMenus.ListMenu;
import me.marcarrots.triviatreasure.menu.subMenus.ParameterMenu;
import me.marcarrots.triviatreasure.menu.subMenus.RewardsSpecificMenu;
import me.marcarrots.triviatreasure.menu.subMenus.ViewMenu;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public enum PromptType {

    SET_ROUNDS(ChatColor.DARK_AQUA + "Enter the number of rounds you'd like this game to have.", ChatColor.GREEN + "The amount of rounds has been modified.", MenuType.PARAMETER_MENU),
    SET_TIME(ChatColor.DARK_AQUA + "Enter the time for each round.", ChatColor.GREEN + "The time for each round has been modified.", MenuType.PARAMETER_MENU),
    NEW_ENTRY_QUESTION(ChatColor.DARK_AQUA + "Enter the question you'd like to use. Type" + ChatColor.RED + " 'back' " + ChatColor.DARK_AQUA + "to return.", null, MenuType.LIST_MENU),
    NEW_ENTRY_ANSWER(ChatColor.DARK_AQUA + "Enter the answers you'd like to use for this question. For multiple answers, separate by comma.", ChatColor.GREEN + "This question has been added to the pool.", MenuType.LIST_MENU),
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

    public void openNewMenu(TriviaTreasure triviaTreasure, Player player, int place) {
        switch (menu) {
            case MAIN_MENU:
                break;
            case LIST_MENU:
                new ListMenu(triviaTreasure, player).open();
                break;
            case VIEW_MENU:
                new ViewMenu(triviaTreasure, player).open();
                break;
            case PARAMETER_MENU:
                new ParameterMenu(triviaTreasure, player).open();
                break;
            case REWARDS_MENU:
                new RewardsSpecificMenu(triviaTreasure, player, place).open();
                break;
        }
    }
}
