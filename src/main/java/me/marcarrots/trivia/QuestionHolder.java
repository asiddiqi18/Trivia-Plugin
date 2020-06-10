package me.marcarrots.trivia;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class QuestionHolder {

    private final List<Question> triviaQuestionList;

    private boolean uniqueQuestions;

    public QuestionHolder() {
        triviaQuestionList = new ArrayList<>();
    }

    public QuestionHolder(QuestionHolder questionHolder) {
        triviaQuestionList = new ArrayList<>();
        triviaQuestionList.addAll(questionHolder.getTriviaQuestionList());
        uniqueQuestions = questionHolder.isUniqueQuestions();

    }

    public void add(Question triviaQuestion) {
        triviaQuestionList.add(triviaQuestion);
    }

    public int getSize() {
        try {
            return triviaQuestionList.size();
        } catch (NullPointerException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public Question getQuestion(int index) {
        return triviaQuestionList.get(index);
    }


    public Question getRandomQuestion() {
        Random random = new Random();
        int randomIndex = random.nextInt(getSize());
        Question question = getQuestion(randomIndex);
        if (uniqueQuestions) {
            triviaQuestionList.remove(randomIndex);
        }
        return question;
    }

    public List<Question> getTriviaQuestionList() {
        return triviaQuestionList;
    }


    public boolean isUniqueQuestions() {
        return uniqueQuestions;
    }

    public void setUniqueQuestions(boolean uniqueQuestions) {
        this.uniqueQuestions = uniqueQuestions;
    }

}
