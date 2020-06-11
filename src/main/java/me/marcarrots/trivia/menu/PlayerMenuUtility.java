package me.marcarrots.trivia.menu;

import me.marcarrots.trivia.Question;
import org.bukkit.entity.Player;

public class PlayerMenuUtility {

    private Player owner;

    private int totalRounds;

    private long timePer;

    private boolean repeatEnabled;
    private Question question;
    private MenuType previousMenu = null;

    public PlayerMenuUtility(Player owner) {
        this.owner = owner;
        totalRounds = 4;
        timePer = 10;
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

    public void setAnswerString(String newAnswer) {
        question.setAnswer(newAnswer);
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

    public Player getOwner() {
        return owner;
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

