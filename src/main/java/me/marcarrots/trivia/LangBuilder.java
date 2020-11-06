/*
 * Trivia by MarCarrot, 2020
 */

package me.marcarrots.trivia;

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

    public LangBuilder setPlayer(Player player) {
        this.player = player;
        return this;
    }

    public LangBuilder setQuestion(String question) {
        this.questionString = question;
        return this;
    }

    public LangBuilder setAnswer(String answer) {
        this.answerString = answer;
        return this;
    }

    public LangBuilder setQuestionNum(int questionNum) {
        this.questionNum = questionNum;
        return this;
    }

    public LangBuilder setTotalQuestionNum(int totalQuestionNum) {
        this.totalQuestionNum = totalQuestionNum;
        return this;
    }

    public LangBuilder setPoints(int points) {
        this.points = points;
        return this;
    }

    public LangBuilder setElapsedTime(String time) {
        this.elapsedTime = time;
        return this;
    }

    public LangBuilder setVal(String val) {
        this.val = val;
        return this;
    }

    public Player getPlayer() {
        return player;
    }

    public int getQuestionNum() {
        return questionNum;
    }

    public int getTotalQuestionNum() {
        return totalQuestionNum;
    }

    public int getPoints() {
        return points;
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

    public String getVal() {
        return val;
    }
}
