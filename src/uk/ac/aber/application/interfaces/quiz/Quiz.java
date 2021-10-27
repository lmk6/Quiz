package uk.ac.aber.application.interfaces.quiz;

import uk.ac.aber.application.interfaces.QuestionBank;
import uk.ac.aber.application.interfaces.questiontypes.FillTheBlanksQuestion;
import uk.ac.aber.application.interfaces.questiontypes.Question;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Runs quiz and all its mechanics in chosen mode
 *
 * @author lmk6
 * @version 1.0
 */
public class Quiz {
    QuestionBank chosenQuestionBank;
    ArrayList<Question> questions;
    ResourceBundle language;
    Scanner input = new Scanner(System.in);
    private int score;
    private int numOfAnswers;
    private int numOfQuestions;
    private ArrayList<String[]> userAnswersList;

    /**
     * Quiz constructor
     *
     * @param qb             Question Bank chosen by user
     * @param language       chosen previously by user
     * @param numOfQuestions chosen by user
     */
    public Quiz(QuestionBank qb, ResourceBundle language, int numOfQuestions) {
        chosenQuestionBank = qb;
        this.language = language;
        this.numOfQuestions = numOfQuestions;
    }

    /**
     * sets quiz mode to Fill The Blanks
     *
     * @throws Exception when question bank is somehow empty
     */
    public void takeFTBQuiz() throws Exception {
        questions = chosenQuestionBank.getFTBQuestions(language.getLocale().getLanguage());
        questions = getNQuestions();
        if (questions.isEmpty()) throw new Exception(language.getString("noFTBQuestions"));
        runQuiz(true);
    }

    /**
     * sets quiz mode to Single Choice
     *
     * @throws Exception when question bank is somehow empty
     */
    public void takeSCQuiz() throws Exception {
        questions = chosenQuestionBank.getSCQuestions(language.getLocale().getLanguage());
        questions = getNQuestions();
        if (questions.isEmpty()) throw new Exception(language.getString("noSCQuestions"));
        runQuiz(false);
    }

    private void runQuiz(boolean manyAnswers) {
        System.out.println(numOfQuestions + language.getString("nQuestionsToDisplay"));
        System.out.println();
        Collections.shuffle(questions);
        boolean quizRuns = true;
        int numOfUnansweredQ;
        userAnswersList = new ArrayList<>();
        for (int i = 0; i < questions.size(); i++) {
            userAnswersList.add(null);
        }
        score = 0;
        numOfAnswers = 0;
        int qIndex = 0;
        String choice;
        String regex;
        long time = System.currentTimeMillis();
        do {
            displayQuestionMenu(qIndex);
            if (qIndex != questions.size() - 1 && qIndex != 0) regex = "[ANPFanpf]";
            else if (qIndex != questions.size() - 1) regex = "[ANFanf]";
            else if (qIndex != 0) regex = "[APFapf]";
            else regex = "[AFaf]";

            choice = getString(regex, language.getString("enter"));
            switch (choice.toLowerCase(Locale.ROOT)) {
                case "a" -> answerQuestion(manyAnswers, questions.get(qIndex));
                case "n" -> qIndex++;
                case "p" -> qIndex--;
                case "f" -> quizRuns = false;
            }
            System.out.println();
        } while (quizRuns);
        time = System.currentTimeMillis() - time;
        time /= 1000;
        numOfUnansweredQ = checkAnswers(userAnswersList);
        countAnswers();
        System.out.println("\n" + language.getString("time") + getTime(time));
        System.out.println(language.getString("score") + score + " / " + numOfAnswers);
        System.out.println(language.getString("unanswered") + numOfUnansweredQ);
    }

    private int checkAnswers(ArrayList<String[]> userAnswersList) {
        int notAnswered = 0;
        Question current;

        for (int i = 0; i < questions.size(); i++) {
            if (userAnswersList.get(i) != null) {
                current = questions.get(i);
                score += current.getScore(userAnswersList.get(i));
            } else notAnswered++;
        }

        return notAnswered;
    }

    private void answerQuestion(boolean manyAnswers, Question q) {
        System.out.println(q);
        String[] userAnswers;
        if (manyAnswers) {
            userAnswers = new String[q.getNumOfAns()];
            for (int i = 1; i <= q.getNumOfAns(); i++) {
                userAnswers[i - 1] = getString(".*", language.getString("enterNAnswer") + " " + i + ") ");
            }
        } else {
            userAnswers = new String[1];
            userAnswers[0] = getString(".*", language.getString("enterA"));
        }
        userAnswersList.set(questions.indexOf(q), userAnswers);
    }

    private void countAnswers() {
        for (Question q : questions) {
            if (q instanceof FillTheBlanksQuestion) {
                FillTheBlanksQuestion ftbQ = (FillTheBlanksQuestion) q;
                numOfAnswers += ftbQ.getNumOfPossiblePoints();
            } else numOfAnswers++;
        }
    }

    private void displayQuestionMenu(int index) {
        System.out.println(index + 1 + ". " + questions.get(index).toString());
        System.out.println(language.getString("answerQ"));
        if (index != questions.size() - 1) System.out.println(language.getString("nextQ"));
        if (index != 0) System.out.println(language.getString("previousQ"));
        System.out.println(language.getString("finishQ"));
    }

    private ArrayList<Question> getNQuestions() {
        if (numOfQuestions > questions.size()) numOfQuestions = questions.size();
        ArrayList<Question> randomized = new ArrayList<>();
        Question random;
        for (int i = 0; i < numOfQuestions; i++) {
            random = getRandomQuestion();
            while (randomized.contains(random)) {
                random = getRandomQuestion();
            }
            randomized.add(random);
        }
        return randomized;
    }

    private Question getRandomQuestion() {
        int randomIndex = ThreadLocalRandom.current().nextInt(0, questions.size());
        return questions.get(randomIndex);
    }

    private String getString(String regex, String message) {
        String userInp;
        while (true) {
            System.out.print(message);
            userInp = input.nextLine();
            if (userInp.matches(regex)) {
                return userInp;
            }
            System.out.println(language.getString("wrongInput"));
        }
    }

    private String getTime(long time) {
        String formattedTime;
        int hours = (int) time / 3600;
        time %= 3600;
        int minutes = (int) time / 60;
        time %= 60;
        int seconds = (int) time;
        if (hours > 0) {
            formattedTime = hours + "h " + minutes + "min " + seconds + "s";
        } else if (minutes > 0) {
            formattedTime = minutes + " minutes " + seconds + " seconds";
        } else formattedTime = seconds + " seconds";
        return formattedTime;
    }
}
