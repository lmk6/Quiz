package uk.ac.aber.application.interfaces.questiontypes;

import org.json.JSONObject;

/**
 * super abstract class that allows further development (more types of questions)
 *
 * @author lmk6
 * @version 1.0
 */
public abstract class Question {
    String questionContent;

    /**
     * Basic constructor
     *
     * @param questionContent the question itself
     */
    public Question(String questionContent) {
        this.questionContent = questionContent;
    }

    /**
     * @return question itself
     */
    public final String getQuestionContent() {
        return questionContent;
    }

    /**
     * transforms Question into the JSON object
     *
     * @return JSON Object
     */
    public JSONObject getJSON() {
        JSONObject question = new JSONObject();
        question.put("question", questionContent);
        return question;
    }

    /**
     * @param questionJSON transforms JSON object into Question object
     */
    public abstract void extractJSON(JSONObject questionJSON);

    /**
     * @param ans user's answers
     * @return score
     */
    public abstract int getScore(String[] ans);

    /**
     * @return number of answers per question
     */
    public abstract int getNumOfAns();

    /**
     * @return question contents
     */
    @Override
    public String toString() {
        return questionContent;
    }
}
