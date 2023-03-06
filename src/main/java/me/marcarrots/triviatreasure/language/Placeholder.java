/*
 * Trivia by MarCarrot, 2020
 */

package me.marcarrots.triviatreasure.language;

import org.bukkit.entity.Player;

import java.util.List;

public class Placeholder {

    private final Player player;
    private final String questionString;
    private final List<String> answerString;
    private final int questionNum;
    private final int totalQuestionNum;
    private final int points;
    private final String elapsedTime;
    private final String val;

    private Placeholder(PlaceholderBuilder builder) {
        this.player = builder.player;
        this.questionString = builder.questionString;
        this.answerString = builder.answerString;
        this.questionNum = builder.questionNum;
        this.totalQuestionNum = builder.totalQuestionNum;
        this.points = builder.points;
        this.elapsedTime = builder.elapsedTime;
        this.val = builder.val;
    }

    public Player getPlayer() {
        return player;
    }

    public String getQuestionString() {
        return questionString;
    }

    public List<String> getAnswerString() {
        return answerString;
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

    public String getElapsedTime() {
        return elapsedTime;
    }

    public String getVal() {
        return val;
    }

    public static class PlaceholderBuilder {
        private Player player;
        private String questionString;
        private List<String> answerString;
        private int questionNum;

        private int totalQuestionNum;
        private int points;
        private String elapsedTime;
        private String val;

        public PlaceholderBuilder() {
        }

        public Placeholder build() {
            return new Placeholder(this);
        }

        public PlaceholderBuilder question(String question) {
            this.questionString = question;
            return this;
        }

        public PlaceholderBuilder answer(List<String> answer) {
            this.answerString = answer;
            return this;
        }

        public PlaceholderBuilder player(Player player) {
            this.player = player;
            return this;
        }

        public PlaceholderBuilder questionNum(int questionNum) {
            this.questionNum = questionNum;
            return this;
        }

        public PlaceholderBuilder totalQuestionNum(int totalQuestionNum) {
            this.totalQuestionNum = totalQuestionNum;
            return this;
        }

        public PlaceholderBuilder points(int points) {
            this.points = points;
            return this;
        }

        public PlaceholderBuilder elapsedTime(String time) {
            this.elapsedTime = time;
            return this;
        }

        public PlaceholderBuilder val(String val) {
            this.val = val;
            return this;
        }

    }


}
