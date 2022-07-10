package me.marcarrots.trivia;

import me.marcarrots.trivia.data.FileManager;
import me.marcarrots.trivia.menu.PromptType;
import org.bukkit.Bukkit;

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

    public QuestionContainer() {
        this.triviaQuestionList = new ArrayList<>();
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


    public void readQuestions(FileManager questionsFile) {

        triviaQuestionList.clear();

        questionsFile.reloadFiles();
        for (String key : questionsFile.getData().getKeys(false)) {
            try {
                Question triviaQuestion = new Question();
                triviaQuestion.setId(Integer.parseInt(key));
                extractLargestQuestionNum(key);
                triviaQuestion.setQuestion(questionsFile.getData().getString(key + ".question"));
                triviaQuestion.setAnswer(questionsFile.getData().getStringList(key + ".answer"));
                triviaQuestion.setAuthor(questionsFile.getData().getString(key + ".author"));
                triviaQuestionList.add(triviaQuestion);
            } catch (NumberFormatException | NullPointerException e) {
                Bukkit.getLogger().log(Level.SEVERE, String.format("Error with interpreting '%s': Invalid ID. (%s)", key, e.getMessage()));
            }
        }

    }

    public void writeQuestions(Trivia trivia, String questionString, List<String> answerStrings, String author) {
        HashMap<String, Object> questionMap = new HashMap<>();
        questionMap.put("question", questionString);
        questionMap.put("answer", answerStrings);
        if (author != null) {
            questionMap.put("author", author);
        }

        largestQuestionNum++;

        trivia.getQuestionsFile().getData().createSection(String.valueOf(largestQuestionNum), questionMap);
        trivia.getQuestionsFile().saveData();

        Question question = new Question();
        question.setQuestion(questionString);
        question.setAnswer(answerStrings);
        question.setAuthor(author);
        question.setId(largestQuestionNum);
        triviaQuestionList.add(question);

        trivia.getLogger().info(String.format("New question created by %s with question as '%s' and answer(s) as '%s'.", author, questionString, answerStrings));

    }

    private void extractLargestQuestionNum(String questionNum) {
        try {
            if (Integer.parseInt(questionNum) > largestQuestionNum) largestQuestionNum = Integer.parseInt(questionNum);
        } catch (NumberFormatException e) {
            Bukkit.getLogger().log(Level.WARNING, String.format("The key '%s' is invalid and cannot be interpreted.", questionNum));
        }
    }


}
