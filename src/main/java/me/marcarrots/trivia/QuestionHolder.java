package me.marcarrots.trivia;

import me.marcarrots.trivia.menu.PromptType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class QuestionHolder {
    private final List<Question> triviaQuestionList;

    private boolean uniqueQuestions;

    public QuestionHolder() {
        this.triviaQuestionList = new ArrayList<>();
    }

    public QuestionHolder(QuestionHolder questionHolder) {
        this.triviaQuestionList = new ArrayList<>();
        this.triviaQuestionList.addAll(questionHolder.getTriviaQuestionList());
        this.uniqueQuestions = questionHolder.isUniqueQuestions();
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

    private Question getQuestionAtIndex(int index) {
        return triviaQuestionList.get(index);
    }

    public Question getRandomQuestion() {
        Random random = new Random();
        int randomIndex = random.nextInt(getSize());
        Question question = getQuestionAtIndex(randomIndex);
        if (uniqueQuestions)
            triviaQuestionList.remove(randomIndex);
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

    public void clear() {
        triviaQuestionList.clear();
    }

    public Question getQuestion(int id) {
        for (Question q : triviaQuestionList) {
            if (q.getId() == id)
                return q;
        }
        return null;
    }

    public void updateQuestionToFile(Trivia trivia, Question question, String newString, PromptType promptType) {
        switch (promptType) {
            case EDIT_QUESTION:
                trivia.getQuestionsFile().getData().set(question.getId() + ".question", newString);
                break;
            case EDIT_ANSWER:
                trivia.getQuestionsFile().getData().set(question.getId() + ".answer", Arrays.asList(newString.split("\\s*,\\s*")));
                break;
            case DELETE:
                trivia.getQuestionsFile().getData().set(String.valueOf(question.getId()), null);
                break;
        }
        trivia.getQuestionsFile().saveData();
    }
}
