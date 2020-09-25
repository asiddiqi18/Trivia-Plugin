/*
 * Trivia by MarCarrot, 2020
 */

package me.marcarrots.trivia;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ImportQuestions {

    private final Trivia trivia;
    List<String> lineData;
    int questionID;


    public ImportQuestions(Trivia trivia) {
        this.trivia = trivia;
    }

    private void getLineByLine() throws IOException {
        InputStream stream = trivia.getResource("imported_questions.txt");
        if (stream == null) {
            throw new IOException();
        }
        lineData = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8)).lines().collect(Collectors.toList());
    }

    private Question getQuestion() {
        Question question = new Question();
        String line = lineData.get(questionID);
        final String[] split = line.split("/");
        if (split.length != 4) {
            return null;
        }
        question.setQuestion(split[1]);
        question.setAnswer(Arrays.asList(split[2].split("\\s*,\\s*")));
        question.setAuthor(split[3]);
        return question;
    }

    public int getAvailableNumber() throws IOException, NumberFormatException {
        if (lineData == null) {
            getLineByLine();
        }
        return lineData.size();
    }

    public Question readFile(int val) throws IOException, NumberFormatException {
        if (lineData == null) {
            getLineByLine();
        }
        questionID = val - 1;
        if (questionID > lineData.size() || questionID <= 0) {
            throw new NumberFormatException();
        }

        Question question = getQuestion();
        if (question == null) {
            throw new IOException("Received incorrect amount of data read for a line.");
        }
        trivia.writeQuestions(question.getQuestionString(), question.getAnswerList(), question.getAuthor());
        trivia.readQuestions();
        return question;
    }

}
