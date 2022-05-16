package me.marcarrots.trivia;

import me.marcarrots.trivia.menu.PromptType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

// Wrapper List class of 'Question'

public class QuestionHolder {

    private final List<Question> triviaQuestionList;
    private boolean uniqueQuestions;

    public QuestionHolder(QuestionHolder questionHolder) {
        this.triviaQuestionList = new ArrayList<>();
        this.triviaQuestionList.addAll(questionHolder.getTriviaQuestionList());
        this.uniqueQuestions = questionHolder.isUniqueQuestions();
    }

    public QuestionHolder() {
        this.triviaQuestionList = new ArrayList<>();
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

    public void clear() {
        triviaQuestionList.clear();
    }

    public Question getQuestion(int id) {
        int questionIndex = findQuestionIndexByID(id);
        if (questionIndex != -1) {
            return triviaQuestionList.get(questionIndex);
        } else {
            return null;
        }
    }

    private int findQuestionIndexByID(int id) {
        for (int i = 0; i < triviaQuestionList.size(); i++) {
            Question q = triviaQuestionList.get(i);
            if (q.getId() == id) {
                return i;
            }
        }
        return -1;
    }

    public void updateQuestionToFile(Trivia trivia, Question question, String newString, PromptType promptType) {
        int questionIndex = findQuestionIndexByID(question.getId());
        switch (promptType) {
            case EDIT_QUESTION:
                trivia.getQuestionsFile().getData().set(question.getId() + ".question", newString);
                question.setQuestion(newString);
                triviaQuestionList.set(questionIndex, question);
                break;
            case EDIT_ANSWER:
                List<String> strings = Arrays.asList(newString.split("\\s*,\\s*"));
                trivia.getQuestionsFile().getData().set(question.getId() + ".answer", strings);
                question.setAnswer(strings);
                triviaQuestionList.set(questionIndex, question);
                break;
            case DELETE:
                trivia.getQuestionsFile().getData().set(String.valueOf(question.getId()), null);
                triviaQuestionList.remove(questionIndex);
                break;
        }
        trivia.getQuestionsFile().saveData();
    }

}
