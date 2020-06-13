package me.marcarrots.trivia;

import me.marcarrots.trivia.menu.PromptType;
import org.bukkit.Bukkit;

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

    public void clear() {
        triviaQuestionList.clear();
    }

    public Question getQuestion(String question) {
        for (Question q : triviaQuestionList) {
            if (q.getQuestionString().equals(question)) {
                return q;
            }
        }
        return null;
    }

    public void updateQuestionToFile(Trivia trivia, Question question, String newString, PromptType promptType) {

        List<String> unparsedQuestions = trivia.getConfig().getStringList("Questions and Answers");
        if (unparsedQuestions.size() == 0) {
            return;
        }
        String lineString = question.getQuestionString() + " /$/ " + question.getAnswerString();
        for (int i = 0; i < unparsedQuestions.size(); i++) {
            String s = unparsedQuestions.get(i).trim();
            if (s.equals(lineString)) {
                if (promptType == PromptType.EDIT_QUESTION) {
                    unparsedQuestions.set(i, newString + " /$/ " + question.getAnswerString());
                } else if (promptType == PromptType.EDIT_ANSWER) {
                    unparsedQuestions.set(i, question.getQuestionString() + " /$/ " + newString);
                } else if (promptType == PromptType.DELETE) {
                    unparsedQuestions.remove(i);
                } else {
                    throw new IllegalArgumentException();
                }
            }
        }
        trivia.getConfig().set("Questions and Answers", unparsedQuestions);
        trivia.saveConfig();
    }

}
