package uk.ac.aber.application.interfaces;

import uk.ac.aber.application.interfaces.quiz.Quiz;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Allows Student to interact with uk.ac.aber.application
 *
 * @author lmk6
 * @version 1.0
 */
public class StudentI extends UserI {

    /**
     * Basic constructor
     *
     * @param language chosen language
     */
    public StudentI(String language) {
        if (language.equals(super.polish.getLanguage()))
            this.language = ResourceBundle.getBundle(ResourceBundle_filename, polish);
        else this.language = ResourceBundle.getBundle(ResourceBundle_filename, english);
        System.out.println(this.language.getString("welcome"));
    }

    private void displayMenu() {
        System.out.println();
        System.out.println("T - " + language.getString("start"));
        System.out.println("L - " + language.getString("setL"));
        System.out.println("R - " + language.getString("load"));
        System.out.println("Q - " + language.getString("quit"));
        System.out.println();
        System.out.print(language.getString("enter"));
    }

    /**
     * Runs the Student's menu
     */
    @Override
    public void runMenu() {
        String choice;
        do {
            displayMenu();
            choice = input.nextLine().toLowerCase(Locale.ROOT);
            switch (choice) {
                case "t":
                    takeQuiz();
                    break;
                case "l":
                    changeLanguage();
                    break;
                case "r":
                    chooseLoadOption();
                    break;
                case "q":
                    break;
                default:
                    System.out.println(language.getString("wrongInput"));
            }
        } while (!choice.equals("q"));
        System.out.println(language.getString("goodbye"));
    }

    private void takeQuiz() {
        System.out.println(language.getString("quizMode"));
        int choice = getInt(3, language.getString("enter"));
        if (choice == 3) return;
        int numOfQuestions = Integer.parseInt(getString("[0-9]+", language.getString("howMQ"))); //it is impossible to parse something else than integer
        while (numOfQuestions < 1) {                                                                       //due to chosen pattern
            System.out.println(language.getString("tooFewQ"));
            numOfQuestions = Integer.parseInt(getString("[0-9]+", language.getString("howMQ"))); //I used getString(regex, message) to get input and display
        }                                                                                                  //message in one line
        try {
            Quiz quiz = new Quiz(giveBankByModule(new Module(language)), language, numOfQuestions);
            if (choice == 1) quiz.takeFTBQuiz();
            else quiz.takeSCQuiz();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            takeQuiz();
        }
    }

}
