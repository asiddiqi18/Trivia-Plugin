package me.marcarrots.triviatreasure;

import me.marcarrots.triviatreasure.menu.PromptType;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;

import java.util.*;
import java.util.logging.Level;

// Wrapper List class of 'Question'

public class QuestionContainer {

    private final List<Question> triviaQuestionList;
    private boolean uniqueQuestions;
    private int largestQuestionNum;

    public QuestionContainer() {
        this.triviaQuestionList = new ArrayList<>();
    }

    public QuestionContainer(QuestionContainer questionContainer) {
        this.triviaQuestionList = new ArrayList<>();
        this.triviaQuestionList.addAll(questionContainer.getTriviaQuestionList());
        this.uniqueQuestions = questionContainer.isUniqueQuestions();
        this.largestQuestionNum = questionContainer.getLargestQuestionNum();
    }

    public int getLargestQuestionNum() {
        return largestQuestionNum;
    }

    public int getSize() {
        return triviaQuestionList.size();
    }

    public Question getRandomQuestion() {
        Random random = new Random();
        int randomIndex = random.nextInt(getSize());
        Question question = triviaQuestionList.get(randomIndex);
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

    public void updateQuestion(TriviaTreasure triviaTreasure, Question question, String newString, PromptType promptType) {
        int questionIndex = findQuestionIndexByID(question.getId());
        switch (promptType) {
            case EDIT_QUESTION:
                triviaTreasure.getQuestionsFile().getData().set(question.getId() + ".question", newString);
                question.setQuestion(newString);
                triviaQuestionList.set(questionIndex, question);
                break;
            case EDIT_ANSWER:
                List<String> strings = Arrays.asList(newString.split("\\s*,\\s*"));
                triviaTreasure.getQuestionsFile().getData().set(question.getId() + ".answer", strings);
                question.setAnswer(strings);
                triviaQuestionList.set(questionIndex, question);
                break;
            case DELETE:
                triviaTreasure.getQuestionsFile().getData().set(String.valueOf(question.getId()), null);
                triviaQuestionList.remove(questionIndex);
                break;
        }
        triviaTreasure.getQuestionsFile().saveData();
    }

    public void readQuestions(TriviaTreasure triviaTreasure) {
        triviaQuestionList.clear();
        triviaTreasure.getQuestionsFile().reloadFiles();
        for (String key : triviaTreasure.getQuestionsFile().getData().getKeys(false)) {
            try {
                Question triviaQuestion = new Question();
                triviaQuestion.setId(Integer.parseInt(key));
                extractLargestQuestionNum(key);
                ConfigurationSection questionSection = triviaTreasure.getQuestionsFile().getData().getConfigurationSection(key);
                if (questionSection == null) {
                    triviaTreasure.getLogger().warning("Failed to parse key " + key + ".");
                    continue;
                }
                triviaQuestion.setQuestion(questionSection.getString("question"));
                triviaQuestion.setAnswer(questionSection.getStringList("answer"));
                triviaQuestion.setAuthor(questionSection.getString( "author"));
                triviaQuestionList.add(triviaQuestion);
            } catch (NumberFormatException | NullPointerException e) {
                Bukkit.getLogger().log(Level.SEVERE, String.format("Error with interpreting '%s': Invalid ID. (%s)", key, e.getMessage()));
            }
        }
    }

    public void writeQuestions(TriviaTreasure triviaTreasure, String questionString, List<String> answerStrings, String author) {
        HashMap<String, Object> questionMap = new HashMap<>();
        questionMap.put("question", questionString);
        questionMap.put("answer", answerStrings);
        if (author != null) {
            questionMap.put("author", author);
        }

        largestQuestionNum++;

        triviaTreasure.getQuestionsFile().getData().createSection(String.valueOf(largestQuestionNum), questionMap);
        triviaTreasure.getQuestionsFile().saveData();

        Question question = new Question();
        question.setQuestion(questionString);
        question.setAnswer(answerStrings);
        question.setAuthor(author);
        question.setId(largestQuestionNum);
        triviaQuestionList.add(question);

        triviaTreasure.getLogger().info(String.format("New question created by %s with question as '%s' and answer(s) as '%s'.", author, questionString, answerStrings));
    }

    private void extractLargestQuestionNum(String questionNum) {
        try {
            if (Integer.parseInt(questionNum) > largestQuestionNum) largestQuestionNum = Integer.parseInt(questionNum);
        } catch (NumberFormatException e) {
            Bukkit.getLogger().log(Level.WARNING, String.format("The key '%s' is invalid and cannot be interpreted.", questionNum));
        }
    }


}
