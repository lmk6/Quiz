package uk.ac.aber.application;

import uk.ac.aber.application.interfaces.UserI;

import java.io.IOException;

/**
 * Application class - initializes and runs whole program
 * @author lmk6
 * @version 1.0
 */
public class Application {
    private UserI user = new UserI();

    /**
     * runs the interface
     * @throws IOException when there a problem with 'Data.json'
     */
    public void runApp() throws IOException {
        user.setLanguage();
        user = user.setUser();
        user.load("Data.json");
        user.runMenu();
    }

    /**
     * main method
     * @param args by default
     * @throws IOException if any error occurs
     */
    public static void main(String[] args) throws IOException {
        Application app = new Application();
        app.runApp();
    }
}
