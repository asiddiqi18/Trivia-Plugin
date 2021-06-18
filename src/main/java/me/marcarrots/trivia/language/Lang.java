/*
 * Trivia by MarCarrot, 2020
 */

package me.marcarrots.trivia.language;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.Objects;

public enum Lang {
    PREFIX("Prefix", "&6[Trivia]&r"),
    BORDER("Border", "&e&m------------------------------"),
    TRIVIA_START("Trivia Start", "&eTrivia is commencing. Get ready!"),
    TRIVIA_OVER("Trivia Over", "&eTrivia is over!"),
    TRIVIA_ANNOUNCE_WINNER_LIST("Winner List", "&0- &3%player%: &b%points%"),
    TRIVIA_WINNER_MESSAGE("Winner Message", "%border% ; &6Winners: ; %winner_list% ; %border%"),
    TRIVIA_NO_WINNERS("No Winners", "&6There are no winners of this trivia event!"),
    SOLVED_MESSAGE("Solved Message", "&a%player% has answered the question correctly! The answer was &2%answer%&a."),
    TIME_UP("Question Time Up", "&cTime is up! Next question..."),
    QUESTION("Question Display", "&6(%questionNumber%) &e%question%"),
    SKIP("Question Skipped", "&cQuestion skipped! On to the next question..."),
    BOSS_BAR_INFO("Boss Bar Game Info", "Trivia Match (%questionNumber%/%totalQuestions%)"),
    BOSS_BAR_GAME_OVER("Boss Bar Game Over", "Trivia is over!"),
    BOSS_BAR_THANKS("Boss Bar Thanks", "Thanks for playing!"),
    GAME_HALTED("Game Halted", "&cTrivia has been forcibly halted!"),
    RELOAD("Reload Success", "&aAll trivia files have been reloaded."),
    MENU_CHANGE("Menu Change", "&aGo back"),
    MENU_BACK("Menu Back", "&cClick here to change"),
    MENU_CLOSE("Menu Close", "&cClose"),
    MAIN_MENU_TITLE("Main Menu Title", "Trivia Main Menu"),
    MAIN_MENU_START("Main Menu Start", "Start Trivia"),
    MAIN_MENU_START_DESCRIPTION("Main Menu Start Description", "Modify options and get the game started!"),
    MAIN_MENU_REWARDS("Main Menu Rewards", "Rewards"),
    MAIN_MENU_REWARDS_DESCRIPTION("Main Menu Rewards Description", "Adjust trivia prizes given to winners."),
    MAIN_MENU_LIST("Main Menu List", "List Questions"),
    MAIN_MENU_LIST_DESCRIPTION("Main Menu List Description", "Create new questions or modify existing questions."),
    PARAMS_MENU_TITLE("Params Menu Title", "Game parameters"),
    PARAMS_MENU_TOTAL("Params Menu Total", "Total rounds"),
    PARAMS_MENU_TIME("Params Menu Time", "Time per question"),
    PARAMS_MENU_REPEAT("Params Menu Repeat", "Allow question repetition"),
    PARAMS_MENU_START("Params Menu Start", "Start trivia"),
    LIST_MENU_TITLE("List Menu Title", "Trivia Questions (page %val%)"),
    LIST_MENU_QUESTION("List Menu Question", "Question #%val%"),
    LIST_MENU_QUESTION_LORE("List Menu Question Lore", "Question: %val%"),
    LIST_MENU_ANSWER_LORE("List Menu Answer Lore", "Answer: %val%"),
    LIST_MENU_AUTHOR_LORE("List Menu Author Lore", "Submitted by: %val%"),
    LIST_MENU_NEW_QUESTION("List Menu New Question", "(+) Add new Trivia Question"),
    VIEW_MENU_TITLE("View Menu Title", "Trivia Question #%val%"),
    VIEW_MENU_QUESTION("View Menu Question", "Question"),
    VIEW_MENU_ANSWER("View Menu Answer", "Answer"),
    VIEW_MENU_DELETE("View Menu Delete", "Delete this question"),
    REWARDS_GENERAL_MENU_TITLE("Rewards General Menu Title", "Rewards Menu"),
    REWARDS_GENERAL_FIRST("Rewards General First", "First Place"),
    REWARDS_GENERAL_SECOND("Rewards General Second", "Second Place"),
    REWARDS_GENERAL_THIRD("Rewards General Third", "Third Place"),
    REWARDS_SPECIFIC_MONEY("Rewards Specific Money", "Rewarded Money"),
    REWARDS_SPECIFIC_EXP("Rewards Specific Exp", "Rewarded Experience Points"),
    REWARDS_SPECIFIC_MESSAGE("Rewards Specific Message", "Rewarded Message"),


    ;

    private static FileConfiguration LANG;
    private final String path;
    private final String def;

    Lang(String path, String start) {
        this.path = path;
        this.def = start;
    }

    public static void setFile(FileConfiguration config) {
        LANG = config;
    }

    public String getPath() {
        return path;
    }

    public String getDefault() {
        return def;
    }

    public String[] format_multiple(LangBuilder builder) {
        if (LANG == null) {
            return new String[]{""};
        }

        String message = Objects.requireNonNull(LANG.getString(this.path, def));

        String[] items = message.split("\\s*;\\s*");

        for (int i = 0; i < items.length; i++) {
            items[i] = fillPlaceholders(builder, items[i]);
        }

        return items;
    }

    public String format_single(LangBuilder builder) {
        if (LANG == null) {
            return "";
        }

        String message = Objects.requireNonNull(LANG.getString(this.path, def));

        message = fillPlaceholders(builder, message);

        return message;
    }

    public static String fillPlaceholders(LangBuilder builder, String message) {
        message = message.replace("%prefix%", Lang.PREFIX.getDefault());
        message = message.replace("%border%", Lang.BORDER.getDefault());
        if (builder != null) {
            if (builder.getPlayer() != null) {
                message = message.replace("%player%", builder.getPlayer().getDisplayName());
                message = message.replace("%username%", builder.getPlayer().getName());
            }
            if (builder.getQuestionString() != null) {
                message = message.replace("%question%", builder.getQuestionString());
            }
            if (builder.getAnswerString() != null) {
                message = message.replace("%single_answer%", builder.getAnswerString().get(0));
                if (builder.getAnswerString().size() == 1) {
                    message = message.replace("%answer%", String.valueOf(builder.getAnswerString().get(0)));
                } else {
                    message = message.replace("%answer%", String.valueOf(builder.getAnswerString()));
                }
            }
            if (builder.getElapsedTime() != null) {
                message = message.replace("%time_to_answer%", builder.getElapsedTime());
            }
            if (builder.getVal() != null) {
                message = message.replace("%val%", builder.getVal());
            }
            message = message.replace("%questionNumber%", String.valueOf(builder.getQuestionNum()));
            message = message.replace("%totalQuestions%", String.valueOf(builder.getTotalQuestionNum()));
            message = message.replace("%points%", String.valueOf(builder.getPoints()));
        }

        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public static void broadcastMessage(String[] message) {
        for (String s : message) {
            broadcastMessage(s);
        }
    }

    public static void broadcastMessage(String message) {
        if (message.equals("")) {
            return;
        }
        Bukkit.broadcastMessage(message);
    }


}
