package uk.ac.aber.application.interfaces;

import java.util.ResourceBundle;
import java.util.Scanner;

/**
 * Class made for creating Module identifiers
 *
 * @author lmk6
 * @version 1.0
 */
public class Module {
    private String moduleID;

    /**
     * Advanced constructor - asks user to enter the Module
     *
     * @param language allow us to display messages
     */
    public Module(ResourceBundle language) {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print(language.getString("enterModule"));
            moduleID = scanner.nextLine();
            if (checkFormat(moduleID)) break;
            System.out.println(language.getString("wrongModuleFormat"));
        }
    }

    /**
     * Basic constructor
     *
     * @param moduleID unchecked module ID
     * @throws Exception if format of given input is wrong
     */
    public Module(String moduleID) throws Exception {
        if (checkFormat(moduleID)) this.moduleID = moduleID;
        else throw new Exception();
    }

    /**
     * @return moduleID
     */
    public String getModuleID() {
        return moduleID;
    }

    private boolean checkFormat(String input) {
        String variant1 = "[A-Z][A-Z][A-Z][0-9]+";
        String variant2 = "[A-Z][A-Z][0-9]+";
        return (input.matches(variant2) || input.matches(variant1)) && input.length() == 7;
    }

}