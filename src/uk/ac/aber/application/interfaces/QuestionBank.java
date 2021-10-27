package uk.ac.aber.application.interfaces;

import uk.ac.aber.application.interfaces.questiontypes.FillTheBlanksQuestion;
import uk.ac.aber.application.interfaces.questiontypes.Question;
import uk.ac.aber.application.interfaces.questiontypes.SingleChoiceQuestion;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Collection of questions in all languages (2 languages supported in version 1.0)
 *
 * @author lmk6
 * @version 1.0
 */
public class QuestionBank implements Comparable<QuestionBank> {
    Module moduleID;
    String name;
    String bankID;
    private ArrayList<Question[]> questions;
    final static int EN_INDEX = 0;
    final static int PL_INDEX = 1;
    final static int NUM_OF_SUPPORTED_LANG = 2;
    private int index;

    /**
     * Question Bank constructor
     *
     * @param m    module
     * @param name of the Question Bank
     */
    public QuestionBank(Module m, String name) {
        moduleID = m;
        this.name = name;
        bankID = m.getModuleID() + ":" + name;
        questions = new ArrayList<>();
    }

    /**
     * Basic constructor - divides bankID intro name and moduleID
     *
     * @param bankID complete Bank's ID
     * @throws Exception if any input data is not right
     */
    public QuestionBank(String bankID) throws Exception {
        this.bankID = bankID;
        StringBuilder stringBuilder = new StringBuilder();
        String module;
        int ind = 0;
        for (int i = 0; i < bankID.length(); i++) {
            if (bankID.charAt(i) == ':') {
                ind = i;
                break;
            }
            stringBuilder.append(bankID.charAt(i));
        }
        module = stringBuilder.toString();
        this.moduleID = new Module(module);
        stringBuilder = new StringBuilder();
        for (int i = ind + 1; i < bankID.length(); i++) {
            stringBuilder.append(bankID.charAt(i));
        }
        this.name = stringBuilder.toString();
    }

    /**
     * @return bank's ID
     */
    public String getBankID() {
        return bankID;
    }

    /**
     * @return module that bank is associated to
     */
    public String getModuleID() {
        return moduleID.getModuleID();
    }

    /**
     * @return bank's name
     */
    public String getName() {
        return name;
    }

    /**
     * adds another question to the bank (in all supported language variants)
     *
     * @param en question in english
     * @param pl question in polish
     */
    public void addQuestions(Question en, Question pl) {
        Question[] multiLQuestions = new Question[NUM_OF_SUPPORTED_LANG];
        multiLQuestions[EN_INDEX] = en;
        multiLQuestions[PL_INDEX] = pl;
        questions.add(multiLQuestions);
        index++;
    }

    /**
     * @param language currently displayed language
     * @return all Single Choice Questions in chosen language
     */
    public ArrayList<Question> getSCQuestions(String language) {
        ArrayList<Question> sCQuestions = getAllQuestions(language);
        sCQuestions.removeIf(q -> q.getClass() != SingleChoiceQuestion.class);
        return sCQuestions;
    }

    /**
     * @param language currently displayed language
     * @return all Fill the Blanks Questions in chosen language
     */
    public ArrayList<Question> getFTBQuestions(String language) {
        ArrayList<Question> fTBQuestions = getAllQuestions(language);
        fTBQuestions.removeIf(q -> q.getClass() != FillTheBlanksQuestion.class);
        return fTBQuestions;
    }

    /**
     * @param language chosen language
     * @return all Questions that this bank contains
     */
    public ArrayList<Question> getAllQuestions(String language) {
        ArrayList<Question> questions = new ArrayList<>();
        int index;
        switch (language) {
            case "en" -> index = EN_INDEX;
            case "pl" -> index = PL_INDEX;
            default -> {
                System.out.println("unexpected");
                return null;
            }
        }
        for (Question[] q : this.questions) {
            questions.add(q[index]);
        }
        return questions;
    }

    /**
     * @return number of contained questions
     */
    public int getNumOfQuestions() {
        return index;
    }

    /**
     * Transforms QuestionBanks object into JSON object
     *
     * @return JSON object
     */
    public JSONObject getJSON() {
        JSONObject questionBank = new JSONObject();
        questionBank.put("bankID", bankID);
        JSONArray questions = new JSONArray();
        for (Question[] question : this.questions) {
            if (question[EN_INDEX] != null && question[PL_INDEX] != null) {
                JSONArray questionsL = new JSONArray(NUM_OF_SUPPORTED_LANG);
                questionsL.put(question[EN_INDEX].getJSON());
                questionsL.put(question[PL_INDEX].getJSON());
                questions.put(questionsL);
            }
        }
        questionBank.put("questions", questions);
        return questionBank;
    }

    /**
     * Transforms JSON object into QuestionBank
     *
     * @param qb possibly Question Bank in JSON format
     */
    public void extractJSON(JSONObject qb) {
        questions = new ArrayList<>();
        JSONArray questions = new JSONArray();
        questions.putAll(qb.getJSONArray("questions"));
        Question[] multiLQ = new Question[2];
        for (int i = 0; i < questions.length(); i++) {
            JSONArray questionsJSON = questions.getJSONArray(i);
            for (int j = 0; j < questionsJSON.length(); j++) {
                JSONObject questionJSON = questionsJSON.getJSONObject(j);
                String questionItself = questionJSON.getString("question");
                Question question;
                switch (questionJSON.getString("type")) {
                    case "fillTheBlanks" -> question = new FillTheBlanksQuestion(questionItself);
                    case "singleChoice" -> question = new SingleChoiceQuestion(questionItself);
                    default -> throw new IllegalStateException("Unexpected value: " + questionJSON.getString("type"));
                }
                question.extractJSON(questionJSON);
                multiLQ[j] = question;
            }
            this.addQuestions(multiLQ[EN_INDEX], multiLQ[PL_INDEX]);
        }
    }


    /**
     * Removes certain question from the bank
     *
     * @param choice - index of question to be removed
     */
    public void removeQuestion(int choice) {
        try {
            questions.remove(choice);
            index--;
        } catch (Exception e) {                 //if given position is somehow impossible to reach
            System.err.println(e.getMessage());
        }
    }

    /**
     * make QuestionBank sortable
     *
     * @param qb QuestionBank to compare with
     * @return result of comparison
     */
    @Override
    public int compareTo(QuestionBank qb) {
        if (qb != null) {
            return this.bankID.compareTo(qb.bankID);
        }
        return 0;
    }
}
