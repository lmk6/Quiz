package uk.ac.aber.application.interfaces.questiontypes;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Locale;

/**
 * subclass of Question class
 * allows user to read/create single choice question
 *
 * @author lmk6
 * @version 1.0
 */
public class SingleChoiceQuestion extends Question {
    String correctAnswer;
    String[] answers;

    /**
     * Advanced constructor
     *
     * @param questionContent the question itself
     * @param answers         all possible (but not necessarily correct) answers
     * @param correctAnswer   the only correct answer
     */
    public SingleChoiceQuestion(String[] answers, String correctAnswer, String questionContent) {
        super(questionContent);
        this.answers = answers;
        this.correctAnswer = correctAnswer;
    }

    /**
     * Basic constructor
     *
     * @param content the question itself
     */
    public SingleChoiceQuestion(String content) {
        super(content);
    }

    /**
     * Returns the score
     *
     * @param answer user's answer (it's a list to simplify and avoid duplication in code)
     * @return 1 or 0 depending on whether the answer is correct or not
     */
    @Override
    public int getScore(String[] answer) {
        if (answer[0].toLowerCase(Locale.ROOT).trim().equals(correctAnswer.toLowerCase(Locale.ROOT).trim())) return 1;
        else return 0;
    }

    /**
     * @return always one for single choice
     */
    @Override
    public int getNumOfAns() {
        return answers.length;
    }

    /**
     * transforms the object of this class to JSON object
     *
     * @return SingleChoiceQuestion as JSON Object
     */
    @Override
    public JSONObject getJSON() {
        JSONObject sCQ = new JSONObject();
        sCQ.put("type", "singleChoice");
        sCQ.put("question", questionContent);
        JSONArray ans = new JSONArray();
        for (String a : answers) {
            if (!a.isEmpty()) ans.put(a);
        }
        sCQ.put("answers", ans);
        sCQ.put("correctAnswer", correctAnswer);
        return sCQ;
    }

    /**
     * transforms JSON object into SingleChoiceQuestion type of object
     *
     * @param questionJSON JSON version of SingleChoiceQuestion object
     */
    @Override
    public void extractJSON(JSONObject questionJSON) {
        JSONArray answers = new JSONArray(questionJSON.getJSONArray("answers"));
        this.answers = new String[answers.length()];
        for (int i = 0; i < answers.length(); i++) {
            this.answers[i] = answers.getString(i);
        }
        this.correctAnswer = questionJSON.getString("correctAnswer");
    }

    /**
     * @return question's content
     */
    @Override
    public String toString() {
        StringBuilder result = new StringBuilder(super.toString());
        result.append("\n");
        for (int i = 1; i <= answers.length; i++) {
            result.append(i);
            result.append(") ");
            result.append(answers[i - 1]);
            result.append("\n");
        }
        return result.toString();
    }
}
