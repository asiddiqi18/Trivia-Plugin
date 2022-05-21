package me.marcarrots.trivia.menu;

import me.marcarrots.trivia.Question;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class PlayerMenuUtility {

    private int totalRounds;
    private long timePer;
    private boolean repeatEnabled;
    private Question question;
    private MenuType previousMenu = null;

    public PlayerMenuUtility(FileConfiguration config) {
        totalRounds = config.getInt("Default rounds", 10);
        timePer = config.getLong("Default time per round", 10L);
    }

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    public void setQuestionString(String newQuestion) {
        question.setQuestion(newQuestion);
    }

    public void setAnswerString(List<String> newAnswer) {
        this.question.setAnswer(newAnswer);
    }

    public int getTotalRounds() {
        return totalRounds;
    }

    public void setTotalRounds(int totalRounds) {
        if (totalRounds < 0) {
            this.totalRounds = Math.abs(totalRounds);
        } else if (totalRounds == 0) {
            this.totalRounds = 1;
        } else {
            this.totalRounds = totalRounds;
        }
    }

    public long getTimePer() {
        return timePer;
    }

    public void setTimePer(long timePer) {
        if (totalRounds < 0) {
            this.timePer = Math.abs(timePer);
        } else if (timePer == 0) {
            this.timePer = 1;
        } else {
            this.timePer = timePer;
        }
    }

    public MenuType getPreviousMenu() {
        return previousMenu;
    }

    public void setPreviousMenu(MenuType previousMenu) {
        this.previousMenu = previousMenu;
    }

    public boolean isRepeatEnabled() {
        return repeatEnabled;
    }

    public void setRepeatEnabled() {
        repeatEnabled = !repeatEnabled;
    }

}

