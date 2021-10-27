package uk.ac.aber.application.interfaces;

import uk.ac.aber.application.interfaces.questiontypes.FillTheBlanksQuestion;
import uk.ac.aber.application.interfaces.questiontypes.Question;
import uk.ac.aber.application.interfaces.questiontypes.SingleChoiceQuestion;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Allows Teacher to interact with uk.ac.aber.application
 *
 * @author lmk6
 * @version 1.0
 */
public class TeacherI extends UserI {

    /**
     * Basic Constructor
     *
     * @param language chosen language
     */
    public TeacherI(String language) {
        if (language.equals(super.polish.getLanguage()))
            this.language = ResourceBundle.getBundle(ResourceBundle_filename, polish);
        else this.language = ResourceBundle.getBundle(ResourceBundle_filename, english);
        System.out.println(this.language.getString("welcome"));
    }

    private void displayMenu() {
        System.out.println();
        System.out.println("B - " + language.getString("choose"));
        System.out.println("C - " + language.getString("create"));
        System.out.println("L - " + language.getString("setL"));
        System.out.println("S - " + language.getString("save"));
        System.out.println("R - " + language.getString("load"));
        System.out.println("Q - " + language.getString("quit"));
        System.out.println();
        System.out.print(language.getString("enter"));
    }

    /**
     * Runs the Teacher's menu
     *
     * @throws IOException when something goes wrong with loading
     */
    @Override
    public void runMenu() throws IOException {
        String choice;
        do {
            displayMenu();
            choice = input.nextLine().toLowerCase(Locale.ROOT);
            switch (choice) {
                case "b":
                    if (!questionBanks.isEmpty()) chooseExistingBank();
                    else System.out.println(language.getString("noBanks"));
                    break;
                case "c":
                    createNewQuestionBank();
                    break;
                case "l":
                    changeLanguage();
                    break;
                case "s":
                    if (isUserSure()) {
                        save(data_filename);
                        save(backup_filename);   //saving backup manually prevents mistakes
                    }
                    break;
                case "r":
                    chooseLoadOption();
                    break;
                case "q":
                    save(data_filename);
                    break;
                default:
                    System.out.println(language.getString("wrongInput"));
            }
        }
        while (!choice.equals("q"));
        System.out.println(language.getString("goodbye"));
    }

    private void runBankMenu(QuestionBank qb) {
        int choice;
        do {
            System.out.println();
            System.out.println("Question Bank -- " + qb.getBankID());
            int range = 2;
            boolean bool = qb.getNumOfQuestions() == 0;
            System.out.println(language.getString("addQ"));
            if (bool) {
                System.out.println(language.getString("rmQB"));
                range++;
            }
            if (!bool) {
                System.out.println(range + " - " + language.getString("rmQ"));
                range++;
            }
            System.out.println(range + " - " + language.getString("goBack"));
            choice = getInt(range, language.getString("enter"));
            if (choice == 1) addQuestion(qb);
            else if (bool && choice == 2) {
                questionBanks.remove(qb);
                System.out.println(language.getString("bankRemoved"));
                break;
            } else if (!bool && choice == 2) removeQuestion(qb);
        } while (choice != 3);
    }

    private void removeQuestion(QuestionBank questionBank) {
        StringBuilder list = new StringBuilder();
        ArrayList<Question> questions = questionBank.getAllQuestions(language.getLocale().getLanguage());
        int index = 0;
        for (int i = 0; i < questions.size(); i++) {
            index++;
            list.append(index);
            list.append(". ");
            list.append(questions.get(index - 1).getQuestionContent());
            list.append("\n");
        }
        index++;
        list.append(index).append(" - ").append("goBack");
        System.out.println(list);
        int choice = getInt(index, language.getString("enter"));
        if (choice == index) return;
        questionBank.removeQuestion(choice - 1);
        System.out.println(language.getString("questionRemoved"));
    }

    private void addQuestion(QuestionBank qb) {
        Question q_en;
        Question q_pl;
        int numOfAn;
        System.out.println(language.getString("kind"));
        int choice = getInt(2, language.getString("enter"));
        if (choice == 1) {
            boolean isOnePointWorth;
            isOnePointWorth = getString("Y|y|N|n", language.getString("isOnePointWorth")).equalsIgnoreCase("y");
            if (language.getLocale().equals(english)) {      //questions have to be in strict order to ensure that they will display correctly
                q_en = createFTBQuestion(isOnePointWorth);
                changeLanguage();
                System.out.println(language.getString("enterInAnother"));
                q_pl = createFTBQuestion(isOnePointWorth);
            } else {
                q_pl = createFTBQuestion(isOnePointWorth);
                changeLanguage();
                System.out.println(language.getString("enterInAnother"));
                q_en = createFTBQuestion(isOnePointWorth);
            }
        } else {
            numOfAn = Integer.parseInt(getString("[0-9]+", language.getString("howMA")));
            while (numOfAn < 1 || numOfAn > 11) {
                System.out.println(language.getString("notInRange") + " 1-10");
                numOfAn = Integer.parseInt(getString("[0-9]+", language.getString("howMA")));
            }
            if (language.getLocale().equals(english)) {
                q_en = createSCQuestion(numOfAn);
                changeLanguage();
                System.out.println(language.getString("enterInAnother"));
                q_pl = createSCQuestion(numOfAn);
            } else {
                q_pl = createSCQuestion(numOfAn);
                changeLanguage();
                System.out.println(language.getString("enterInAnother"));
                q_en = createSCQuestion(numOfAn);
            }
        }
        changeLanguage();
        qb.addQuestions(q_en, q_pl);
    }

    private SingleChoiceQuestion createSCQuestion(int numOfAn) {
        SingleChoiceQuestion q;
        String question;
        String[] answers;
        String correctAnswer;
        question = getString("^(?!\\s*$).+", language.getString("enterQu"));

        answers = new String[numOfAn];
        for (int i = 1; i <= numOfAn; i++) {
            answers[i - 1] = getString("^(?!\\s*$).+", i + ") ");
        }

        while (true) {
            correctAnswer = getString(".+", language.getString("enterC"));
            try {
                int choice = Integer.parseInt(correctAnswer);
                correctAnswer = answers[choice - 1];
                break;
            } catch (Exception e) {
                System.out.println(language.getString("wrongInput"));
            }
        }
        q = new SingleChoiceQuestion(answers, correctAnswer, question);
        return q;
    }

    private FillTheBlanksQuestion createFTBQuestion(boolean isOnePointWorth) {
        String question;
        String[][] answers;
        question = getString("^(?!\\s*$)(?:_+).+|.+_+.+|.+_$", language.getString("enterQ"));
        int numOfAn = 0;
        Pattern pattern = Pattern.compile("_");
        Matcher matcher = pattern.matcher(question);
        while (matcher.find()) numOfAn++;   //one "_" means one answer
        int numOfPAn = getInt(10, language.getString("howManyPA"));
        answers = new String[numOfAn][numOfPAn];
        for (int i = 0; i < numOfAn; i++) {
            System.out.println(language.getString("blankNo") + (i + 1) + ":");
            for (int j = 0; j < numOfPAn; j++)
                answers[i][j] = getString("^(?!\\s*$).+", (j + 1) + ") ");
        }
        System.out.println();
        return new FillTheBlanksQuestion(question, answers, isOnePointWorth);
    }

    private void chooseExistingBank() {
        QuestionBank chosenOne = null;
        System.out.println(language.getString("chooseB"));
        System.out.println(printAllBanks());
        System.out.println(language.getString("chooseByIndex"));
        System.out.println(language.getString("chooseByName"));
        String choice = getString("I|N|i|n", language.getString("enter")).toLowerCase(Locale.ROOT);
        if (choice.equals("i")) {      //makes it faster to choose when there are only few banks
            int index = getInt(questionBanks.size(), language.getString("enter"));
            chosenOne = questionBanks.get(index - 1);
        } else {
            try {
                chosenOne = giveBankByModule(new Module(language));
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
        if (chosenOne != null)
            runBankMenu(chosenOne);
    }

    private void createNewQuestionBank() {
        Module m;
        String name;
        m = new Module(language);
        name = getString("\\S.*", language.getString("enterBankName"));
        QuestionBank bank = new QuestionBank(m, name);
        if (bankAlreadyExists(bank)) {
            System.out.println(language.getString("bankAlreadyExists"));
            return;
        }
        questionBanks.add(bank);
        System.out.println(language.getString("created") + bank.getBankID());
        System.out.println();
        runBankMenu(bank);
    }

    private String printAllBanks() {
        StringBuilder result = new StringBuilder("\n");
        Collections.sort(questionBanks);
        int index = 1;
        for (QuestionBank q : questionBanks) {
            result.append(index);
            result.append(". ");
            result.append(q.getBankID());
            result.append("\n");
            index++;
        }
        return result.toString();
    }

    private void save(String filename) throws IOException {
        try (FileWriter fw = new FileWriter(filename);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter writer = new PrintWriter(bw)) {
            JSONObject demo = new JSONObject();
            JSONArray questionBanks = new JSONArray();
            for (QuestionBank qb : this.questionBanks) {
                if (qb != null) questionBanks.put(qb.getJSON());
            }
            demo.put("questionBanks", questionBanks);
            writer.println(demo.toString(2));
        }
        System.out.println(language.getString("saved"));
    }
}
