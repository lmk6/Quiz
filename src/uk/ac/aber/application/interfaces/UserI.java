package uk.ac.aber.application.interfaces;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * This class allows user to interact with uk.ac.aber.application
 *
 * @author lmk6
 * @version 1.0
 */
public class UserI {
    ArrayList<QuestionBank> questionBanks;
    Scanner input = new Scanner(System.in);
    Locale english = new Locale("en", "UK");
    Locale polish = new Locale("pl", "POL");
    String ResourceBundle_filename = "uk/ac/aber/application/interfaces/Languages";
    String data_filename = "Data.json";
    String backup_filename = "backup_data.json";
    ResourceBundle language;

    /**
     * sets language to preferred by the user
     * reads from Resource Bundle 'Languages' which is required
     */
    public void setLanguage() {
        language = ResourceBundle.getBundle(ResourceBundle_filename, english);
        String choice = getString("pl|PL|en|EN", "Choose your language [en/pl]: ").toLowerCase(Locale.ROOT);
        if (choice.equals("pl")) language = ResourceBundle.getBundle(ResourceBundle_filename, polish);
        else language = ResourceBundle.getBundle(ResourceBundle_filename, english);
    }


    void changeLanguage() {
        if (language.getLocale().equals(polish)) language = ResourceBundle.getBundle(ResourceBundle_filename, english);
        else language = ResourceBundle.getBundle(ResourceBundle_filename, polish);
    }

    /**
     * sets the user depending on input (teacher or student)
     *
     * @return TeacherInterface or StudentInterface
     */
    public UserI setUser() {
        String choice = getString("y|Y|n|N", language.getString("studentOrTeacher"));
        switch (choice) {
            case "y" -> {
                return new TeacherI(this.getLanguage());
            }

            case "n" -> {
                return new StudentI(this.getLanguage());
            }
            default -> throw new IllegalStateException("Unexpected value: " + choice);
        }
    }

    /**
     * empty class to override by subclass
     *
     * @throws IOException depends on menu
     */
    public void runMenu() throws IOException {

    }

    /**
     * gives the currently set language
     *
     * @return language
     */
    public String getLanguage() {
        if (language != null) return language.getLocale().getLanguage();
        else return null;
    }

    int getInt(int range, String message) {
        int userInp;
        while (true) {
            System.out.print(message);
            try {
                userInp = input.nextInt();
                if (userInp <= range && userInp > 0) {
                    input.nextLine();           //clears the buffer
                    return userInp;
                } else System.out.println(language.getString("notInRange") + " 1-" + range);
            } catch (InputMismatchException e) {
                System.out.println(language.getString("wrongInput"));
            }
            input.nextLine();
        }
    }

    String getString(String regex, String message) {
        String userInp;
        while (true) {
            System.out.print(message);
            userInp = input.nextLine();
            if (userInp.matches(regex)) {   //I have found the use of regular expressions as the best way to keep my code tidy
                return userInp;
            }
            System.out.println(language.getString("wrongInput"));
        }
    }

    /**
     * loads the data from Data.json
     *
     * @param filename of the source
     */
    public void load(String filename) {
        questionBanks = new ArrayList<>();
        try {
            File data = new File(filename);
            if (data.exists()) {
                String contents = new String(Files.readAllBytes(Paths.get(filename)));      //I have used the same solution to read
                JSONObject input = new JSONObject(contents);                                //from file in my Mini Assignment work
                JSONArray questionBanks = new JSONArray().putAll(input.getJSONArray("questionBanks"));
                for (int i = 0; i < questionBanks.length(); i++) {
                    JSONObject questionBank = questionBanks.getJSONObject(i);
                    QuestionBank qb = new QuestionBank(questionBank.getString("bankID"));
                    qb.extractJSON(questionBank);
                    if (!bankAlreadyExists(qb)) this.questionBanks.add(qb);          //Avoiding repetition of bankID
                }
                System.out.println(language.getString("loaded"));

            } else {
                System.out.println(language.getString("cannotLoad"));
            }
        } catch (Exception e) {
            System.out.println(language.getString("unexpected") + "\n" + e);
        }
    }

    boolean bankAlreadyExists(QuestionBank qb) {
        for (QuestionBank bank : questionBanks) {
            if (bank.getBankID().equals(qb.getBankID())) return true;
        }
        return false;
    }

    QuestionBank giveBankByModule(Module m) throws Exception {
        ArrayList<QuestionBank> questionBanks = new ArrayList<>();
        for (QuestionBank q : this.questionBanks) {
            if (q.getModuleID().equals(m.getModuleID())) questionBanks.add(q);
        }
        Collections.sort(questionBanks);
        StringBuilder listOfBanks = new StringBuilder();
        if (questionBanks.isEmpty()) {
            throw new Exception("\n" + language.getString("noSuchBanks") + "\n");
        }
        for (int i = 1; i <= questionBanks.size(); i++) {
            listOfBanks.append(i);
            listOfBanks.append(". ");
            listOfBanks.append(questionBanks.get(i - 1).getBankID());
            listOfBanks.append("\n");
        }
        System.out.println(language.getString("chooseB"));
        System.out.println(listOfBanks);
        String choice = getString(getRegexList(questionBanks), language.getString("enterBankName"));
        QuestionBank chosenQBank = lookForQuestionBank(m.getModuleID() + ":" + choice);
        if (chosenQBank != null) return chosenQBank;
        else throw new NullPointerException();
    }

    void chooseLoadOption() {
        String choice = getString("Y|y|N|n", language.getString("whichFile"));
        if (isUserSure()) {
            if (choice.equalsIgnoreCase("y")) load(backup_filename);
            else load(data_filename);
        }
    }

    boolean isUserSure() {       //method made only for clear looking code
        return getString("Y|y|N|n", language.getString("justInCase")).equalsIgnoreCase("y");
    }

    private String getRegexList(ArrayList<QuestionBank> a) {    //creates a list of possible inputs
        StringBuilder list = new StringBuilder("(?:");          //Bank names in this case
        for (QuestionBank q : a) {
            list.append(q.getName());
            if (a.indexOf(q) == a.size() - 1) list.append(")");
            else list.append("|");
        }
        return list.toString();
    }

    private QuestionBank lookForQuestionBank(String bankID) {
        for (QuestionBank q : questionBanks) {
            if (q.getBankID().equals(bankID)) return q;
        }
        return null;
    }
}