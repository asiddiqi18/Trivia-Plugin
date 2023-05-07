/*
 * Trivia by MarCarrot, 2020
 */

package me.marcarrots.triviatreasure.language;

import org.bukkit.configuration.file.FileConfiguration;

import java.util.Objects;

public enum Lang {
    PREFIX("Plugin Prefix", "&6[Trivia]&r"),
    BORDER("Border", "&e&m------------------------------"),
    TRIVIA_START("Trivia Start", "&eTrivia is commencing. Get ready!"),
    TRIVIA_ANNOUNCE_WINNER_LIST("Winner List", "&0- &3%player%: &b%points%"),
    TRIVIA_WINNER_MESSAGE("Winner Message", "%border% ; &6Winners: ; %winner_list% ; %border%"),
    TRIVIA_NO_WINNERS("No Winners", "&6There are no winners of this trivia event!"),
    SOLVED_MESSAGE("Solved Message", "&a%player% has answered the question correctly! The answer was &2%answer%&a."),
    TIME_UP("Question Time Up", "&cTime is up! Next question..."),
    QUESTION("Question Display", "&6(%questionNumber%) &e%question%"),
    SKIP("Question Skipped", "&cQuestion skipped! On to the next question..."),
    NO_QUESTIONS_LOADED("No Questions Loaded", "&cThere are no Trivia questions loaded. Create some questions before hosting a game!"),
    FORCE_REPEAT("Force Repeat", "&cThere are more rounds than questions, so questions will repeat."),
    BOSS_BAR_START("Boss Bar Start", "Trivia is commencing. Get ready!"),
    BOSS_BAR_INFO("Boss Bar Game Info", "Trivia Match (%questionNumber%/%totalQuestions%)"),
    BOSS_BAR_GAME_OVER("Boss Bar Game Over", "Trivia is over!"),
    BOSS_BAR_THANKS("Boss Bar Thanks", "Thanks for playing!"),
    BOSS_BAR_HALTED("Boss Bar Halted", "Trivia has been stopped!"),
    GAME_HALTED("Game Halted", "&cTrivia has been forcibly halted!"),
    GAME_SUMMARY_MATCH_STARTED("Game Summary.match_started", "&6&lTrivia match started!"),
    GAME_SUMMARY_SUMMARY("Game Summary.summary", "&eGame summary:"),
    GAME_SUMMARY_ROUNDS("Game Summary.rounds", "&0- &eRounds: &f%val%"),
    GAME_SUMMARY_SECONDS_PER("Game Summary.seconds_per", "&0- &eSeconds per round: &f%val%"),
    GAME_SUMMARY_REPEAT_ENABLED("Game Summary.repeat_enabled", "&0- &eRepeat questions: &f%val%"),
    COMMANDS_SCHEDULE("Commands.schedule.display", "&aTime until next trivia game: &e%time% &6(Players needed: %players_needed%)"),
    COMMANDS_SCHEDULE_DISABLED("Commands.schedule.not_enabled", "&cScheduling of games is currently not enabled."),
    COMMANDS_STATS_NAME("Commands.stats.name", "&aTrivia stats for %player%:"),
    COMMANDS_STATS_PARTICIPATED("Commands.stats.participation", " &6-  Number of games participated in: &e%val%"),
    COMMANDS_STATS_ROUNDS_WON("Commands.stats.rounds_won", " &6-  Number of rounds won: &e%val%"),
    COMMANDS_STATS_VICTORIES("Commands.stats.victories", " &6-  Victories"),
    COMMANDS_STATS_FIRST("Commands.stats.first_place", " &6  -  1st place: &e%val%"),
    COMMANDS_STATS_SECOND("Commands.stats.second_place", " &6  -  2nd place: &e%val%"),
    COMMANDS_STATS_THIRD("Commands.stats.third_place", " &6  -  3rd place: &e%val%"),
    COMMANDS_STATS_ENCHANTING("Commands.stats.enchanting", " &6 -  Enchanting exp earned from wins: &e%val%"),
    COMMANDS_STATS_MONEY("Commands.stats.money", " &6 -  Money earned from wins: &e%val%"),
    COMMANDS_RELOAD_CONFIG("Commands.reload.config_file", "&eConfig file reloaded."),
    COMMANDS_RELOAD_QUESTIONS("Commands.reload.questions_file", "&eQuestions file reloaded."),
    COMMANDS_RELOAD_MESSAGES("Commands.reload.messages_file", "&eMessages file reloaded."),
    COMMANDS_RELOAD_REWARDS("Commands.reload.rewards_file", "&eRewards file reloaded."),
    COMMANDS_RELOAD_SUCCESSFUL("Commands.reload.successful", "✔ Successfully reloaded all files!"),
    COMMANDS_RELOAD_FAILURE("Commands.reload.failure", "&cFailed to reload files"),
    COMMANDS_ERROR_NO_PERMISSION("Commands.errors.no_permission", "&cYou do not have permission to use this command."),
    COMMANDS_ERROR_NOT_FOUND("Commands.errors.not_found", "&cCommand not found."),
    COMMANDS_ERROR_GAME_IN_PROGRESS("Commands.errors.game_in_progress", "&cThere is a game already in progress"),
    COMMANDS_ERROR_GAME_NOT_IN_PROGRESS("Commands.errors.game_not_in_progress", "&cThere is no trivia game in progress."),
    COMMANDS_ERROR_SKIP_NOT_ALOWED("Commands.errors.skip_not_allowed", "&cFailed to skip round. Reason:Skip request made in-between rounds."),
    COMMANDS_HELP_PREFIX("Commands.help.prefix", "&6/trivia"),
    COMMANDS_HELP_MAIN("Commands.help.main", " &7- &f(Main command) Opens up the Trivia menu to manage or start trivia."),
    COMMANDS_HELP_HELP("Commands.help.help", " help &7- &fDisplays a help menu describing trivia commands."),
    COMMANDS_HELP_RELOAD("Commands.help.reload", " reload &7- &fReloads all the trivia files."),
    COMMANDS_HELP_SCHEDULE("Commands.help.schedule", " schedule &7- &fGets the time remaining until the next scheduled trivia game begins"),
    COMMANDS_HELP_SKIP("Commands.help.skip", " skip &7- &fSkips the current question during a game."),
    COMMANDS_HELP_START("Commands.help.start", " start <# of rounds>&7 - &fQuickly starts up a trivia game. Optionally, specify number of rounds."),
    COMMANDS_HELP_STATS("Commands.help.stats", " stats &7- &fShows your trivia stats record."),
    COMMANDS_HELP_STOP("Commands.help.stop", " stop &7- &fStops the current trivia game in progress."),
    COMMANDS_HELP_VERSION("Commands.help.version", " version &7- &fGets the current plugin's version number."),
    MENU_CHANGE("Menu Change", "&aGo back"),
    MENU_BACK("Menu Back", "&cClick here to change"),
    MENU_CLOSE("Menu Close", "&cClose"),
    MAIN_MENU_TITLE("Main Menu Title", "Trivia Treasure Main Menu"),
    MAIN_MENU_START("Main Menu Start", "Start Trivia"),
    MAIN_MENU_START_DESCRIPTION("Main Menu Start Description", "Modify options and get the game started!"),
    MAIN_MENU_REWARDS("Main Menu Rewards", "Modify Rewards"),
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
    REWARDS_SPECIFIC_FIREWORKS("Rewards Specific Firework", "Summon Fireworks on Victory"),
    REWARDS_SPECIFIC_MESSAGE("Rewards Specific Message", "Rewarded Message");

    private static FileConfiguration LANG;
    private final String path;
    private final String def;

    Lang(String path, String def) {
        this.path = path;
        this.def = def;
    }

    public static void setFile(FileConfiguration config) {
        LANG = config;
    }

    public static String fillPlaceholders(Placeholder placeholder, String message) {
        message = message.replace("%prefix%", Lang.PREFIX.getVal());
        message = message.replace("%border%", Lang.BORDER.getVal());
        if (placeholder != null) {
            if (placeholder.getPlayer() != null) {
                message = message.replace("%player%", placeholder.getPlayer().getDisplayName());
                message = message.replace("%username%", placeholder.getPlayer().getName());
            }
            if (placeholder.getQuestionString() != null) {
                message = message.replace("%question%", placeholder.getQuestionString());
            }
            if (placeholder.getAnswerString() != null) {
                message = message.replace("%single_answer%", placeholder.getAnswerString().get(0));
                if (placeholder.getAnswerString().size() == 1) {
                    message = message.replace("%answer%", String.valueOf(placeholder.getAnswerString().get(0)));
                } else {
                    message = message.replace("%answer%", String.valueOf(placeholder.getAnswerString()));
                }
            }
            if (placeholder.getElapsedTime() != null) {
                message = message.replace("%time_to_answer%", placeholder.getElapsedTime());
            }
            if (placeholder.getVal() != null) {
                message = message.replace("%val%", placeholder.getVal());
            }
            message = message.replace("%questionNumber%", String.valueOf(placeholder.getQuestionNum()));
            message = message.replace("%totalQuestions%", String.valueOf(placeholder.getTotalQuestionNum()));
            message = message.replace("%points%", String.valueOf(placeholder.getPoints()));
        }

        return MessageUtil.HexGradient(message);
    }

    public String getPath() {
        return path;
    }

    public String getDefault() {
        return def;
    }

    public String getVal() {
        return LANG.getString(path, def);
    }

    public String[] format_multiple(Placeholder placeholder) {
        if (LANG == null) {
            return new String[]{""};
        }

        String message = Objects.requireNonNull(LANG.getString(path, def));

        String[] items = message.split("\\s*;\\s*");

        for (int i = 0; i < items.length; i++) {
            items[i] = fillPlaceholders(placeholder, items[i]);
        }

        return items;
    }

    public String format_single() {
        return format_single(null);
    }

    public String format_single(Placeholder placeholder) {
        if (LANG == null) {
            return "";
        }

        String message = Objects.requireNonNull(LANG.getString(this.path, def));

        message = fillPlaceholders(placeholder, message);

        return message;
    }


}
