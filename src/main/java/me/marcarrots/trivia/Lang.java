package me.marcarrots.trivia;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.Objects;

public enum Lang {
    TRIVIA_START("Messages.Trivia-Start", "&eTrivia is commencing. Get ready!"),
    TRIVIA_OVER("Messages.Trivia-Over", "&eTrivia is over!"),
    TRIVIA_OVER_WINNER_LINE("Messages.Winner-Line", "&6Winners:"),
    TRIVIA_ANNOUNCE_WINNER_LIST("Messages.Winner-List", "&0➤ &3%player%: &b%points%"),
    TRIVIA_NO_WINNERS("Messages.No-Winners", "&6There are no winners of this trivia event!"),
    SOLVED_MESSAGE("Messages.Solved-Message", "&a%player% has answered the question correctly! The answer was &2%answer%&a."),
    TIME_UP("Messages.Question-Time-Up", "&cTime is up! Next question..."),
    QUESTION("Messages.Question-Display", "&6(%questionNumber%) &e%question%"),

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


    public String format() {
        if (LANG == null) {
            return "";
        }
        return ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(LANG.getString(this.path, def)));
    }


    public String format(String playerName, String question, String answer, int questionNum, int points) {
        if (LANG == null) {
            return "";
        }
        String message = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(LANG.getString(this.path, def)));
        if (playerName != null) {
            message = message.replace("%player%", playerName);
        }
        if (question != null) {
            message = message.replace("%question%", question);
        }
        if (answer != null) {
            message = message.replace("%answer%", answer);
        }
        message = message.replace("%questionNumber%", String.valueOf(questionNum));
        message = message.replace("%points%", String.valueOf(points));

        return message;
    }

}
