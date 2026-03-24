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
    private final Integer questionNum;
    private final Integer totalQuestionNum;
    private final Integer points;
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

    public Integer getQuestionNum() {
        return questionNum;
    }

    public Integer getTotalQuestionNum() {
        return totalQuestionNum;
    }

    public Integer getPoints() {
        return points;
    }

    public String getElapsedTime() {
        return elapsedTime;
    }

    public String getVal() {
        return val;
    }

    public String apply(String message) {
        message = message.replace("%prefix%", Lang.PREFIX.getVal());
        message = message.replace("%border%", Lang.BORDER.getVal());
        if (player != null) {
            message = message.replace("%player%", player.getDisplayName());
            message = message.replace("%username%", player.getName());
        }
        if (questionString != null) {
            message = message.replace("%question%", questionString);
        }
        if (answerString != null) {
            message = message.replace("%single_answer%", answerString.get(0));
            if (answerString.size() == 1) {
                message = message.replace("%answer%", answerString.get(0));
            } else {
                message = message.replace("%answer%", String.valueOf(answerString));
            }
        }
        if (elapsedTime != null) {
            message = message.replace("%time_to_answer%", elapsedTime);
        }
        if (val != null) {
            message = message.replace("%val%", val);
        }
        if (questionNum != null) {
            message = message.replace("%questionNumber%", String.valueOf(questionNum));
        }
        if (totalQuestionNum != null) {
            message = message.replace("%totalQuestions%", String.valueOf(totalQuestionNum));
        }
        if (points != null) {
            message = message.replace("%points%", String.valueOf(points));
        }
        return MessageUtil.HexGradient(message);
    }

    public static class PlaceholderBuilder {
        private Player player;
        private String questionString;
        private List<String> answerString;
        private Integer questionNum;
        private Integer totalQuestionNum;
        private Integer points;
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
