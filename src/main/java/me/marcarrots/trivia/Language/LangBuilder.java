/*
 * Trivia by MarCarrot, 2020
 */

/*
 * Trivia by MarCarrot, 2020
 */

package me.marcarrots.trivia.Language;

import org.bukkit.entity.Player;

public class LangBuilder {
    private Player player;
    private String questionString;
    private String answerString;
    private int questionNum;

    private int totalQuestionNum;
    private int points;
    private String elapsedTime;
    private String val;

    public LangBuilder setQuestion(String question) {
        this.questionString = question;
        return this;
    }

    public LangBuilder setAnswer(String answer) {
        this.answerString = answer;
        return this;
    }

    public Player getPlayer() {
        return player;
    }

    public LangBuilder setPlayer(Player player) {
        this.player = player;
        return this;
    }

    public int getQuestionNum() {
        return questionNum;
    }

    public LangBuilder setQuestionNum(int questionNum) {
        this.questionNum = questionNum;
        return this;
    }

    public int getTotalQuestionNum() {
        return totalQuestionNum;
    }

    public LangBuilder setTotalQuestionNum(int totalQuestionNum) {
        this.totalQuestionNum = totalQuestionNum;
        return this;
    }

    public int getPoints() {
        return points;
    }

    public LangBuilder setPoints(int points) {
        this.points = points;
        return this;
    }

    public String getQuestionString() {
        return questionString;
    }

    public String getAnswerString() {
        return answerString;
    }

    public String getElapsedTime() {
        return elapsedTime;
    }

    public LangBuilder setElapsedTime(String time) {
        this.elapsedTime = time;
        return this;
    }

    public String getVal() {
        return val;
    }

    public LangBuilder setVal(String val) {
        this.val = val;
        return this;
    }
}
