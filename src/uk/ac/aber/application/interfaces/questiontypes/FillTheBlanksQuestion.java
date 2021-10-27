package uk.ac.aber.application.interfaces.questiontypes;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Locale;

/**
 * subclass of Question class
 * allows user to read/create question with blanks to fill
 *
 * @author lmk6
 * @version 1.0
 */
public class FillTheBlanksQuestion extends Question {

    boolean isOnePointWorth;
    String[][] answers;

    /**
     * Advanced constructor
     *
     * @param content         the question itself
     * @param answers         all of the correct words to be filled in
     * @param isOnePointWorth decides whether program counts every correct answer or all of them as one correct answer
     */
    public FillTheBlanksQuestion(String content, String[][] answers, boolean isOnePointWorth) {
        super(content);
        this.answers = answers;
        this.isOnePointWorth = isOnePointWorth;
    }

    /**
     * Basic constructor
     *
     * @param content the question itself
     */
    public FillTheBlanksQuestion(String content) {
        super(content);
    }

    /**
     * transforms the object of this class to JSON object
     *
     * @return FillTheBlanksQuestion as JSON Object
     */
    public JSONObject getJSON() {
        JSONObject fTBQ = new JSONObject();
        fTBQ.put("type", "fillTheBlanks");
        fTBQ.put("max1Point", isOnePointWorth);
        fTBQ.put("question", questionContent);
        JSONArray ans = new JSONArray();
        JSONArray possibleAns = new JSONArray();
        for (String[] answer : answers) {
            for (String s : answer)
                if (!s.isEmpty()) {
                    possibleAns = new JSONArray();
                    possibleAns.put(s);
                }
            ans.put(possibleAns);
        }
        fTBQ.put("answers", ans);
        return fTBQ;
    }

    /**
     * gives the number of possible points
     *
     * @return 1 or number of correct answers
     */
    public int getNumOfPossiblePoints() {
        if (isOnePointWorth) return 1;
        else return answers.length;
    }

    /**
     * transforms JSON object into FillTheBlanksQuestion type of object
     *
     * @param questionJSON JSON version of FillTheBlanksQuestion object
     */
    @Override
    public void extractJSON(JSONObject questionJSON) {
        isOnePointWorth = questionJSON.getBoolean("max1Point");
        JSONArray answers = new JSONArray(questionJSON.getJSONArray("answers"));
        this.answers = new String[answers.length()][new JSONArray(answers.getJSONArray(0)).length()];
        for (int i = 0; i < answers.length(); i++) {
            JSONArray possibleAnswers = new JSONArray((answers.getJSONArray(i)));
            for (int j = 0; j < possibleAnswers.length(); j++) this.answers[i][j] = possibleAnswers.getString(j);
        }
    }

    /**
     * Returns the score
     *
     * @param ans user's answers
     * @return the score
     */
    @Override
    public int getScore(String[] ans) {
        int score = 0;
        for (int i = 0; i < ans.length; i++) {
            for (int j = 0; j < answers[i].length; j++)
                if (ans[i].toLowerCase(Locale.ROOT).trim().equals(answers[i][j].toLowerCase(Locale.ROOT).trim())) {
                    score++;
                    break;
                }
        }
        if (isOnePointWorth) {
            if (ans.length == score) return 1;
            else return 0;
        }
        return score;
    }

    /**
     * @return number of answers for question
     */
    @Override
    public int getNumOfAns() {
        return answers.length;
    }

    /**
     * @return question's content
     */
    @Override
    public String toString() {
        return super.toString();
    }
}
