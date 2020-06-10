package me.marcarrots.trivia;

public class Question {

    private String question;

    private String answer;

    public Question() {
    }

    public Question(String question, String answer) {
        this.question = question;
        this.answer = answer;
    }

    public Question getQuestionObj() {
        return this;
    }

    public String getQuestionString() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswerString() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    @Override
    public String toString() {
        return "Question: " + question + ", Answer: " + answer;
    }

}
