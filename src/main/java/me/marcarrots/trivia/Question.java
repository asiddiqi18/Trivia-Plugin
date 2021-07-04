package me.marcarrots.trivia;

import java.util.List;
import java.util.Objects;

public class Question {

    private String question;
    private int id;
    private List<String> answer = null;
    private String author;

    public Question() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
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

    public List<String> getAnswerList() {
        return this.answer;
    }

    public void setAnswer(List<String> answer) {
        this.answer = Objects.requireNonNull(answer);
    }

    @Override
    public String toString() {
        return "Question: " + question + ", Answer: " + answer;
    }

}
