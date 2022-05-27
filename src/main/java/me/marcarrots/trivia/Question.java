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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Question question1 = (Question) o;
        return id == question1.id && Objects.equals(question, question1.question) && Objects.equals(answer, question1.answer) && Objects.equals(author, question1.author);
    }

    @Override
    public int hashCode() {
        return Objects.hash(question, id, answer, author);
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
