package net.grayswander.streamutils.streamtry;

import java.io.IOException;

/**
 * Example functions to test successful, checked and unchecked exception executions
 */
public class ExampleProcessingFunctions {
    public static String successFunc(String input) {
        return "Success: " + input;
    }

    public static String uncheckedDivisionByZeroFunc(String input) {
        int bzz = 1 / 0;
        return "Should never get here: " + input;
    }

    public static String checkedIoExceptionFunc(String input) throws IOException {
        if(true) {
            throw new IOException("Invalid input: " + input);
        }
        return "Should never get here: " + input;
    }

    public static String processFunc(String input) throws IOException {
        switch (input) {
            case "unchecked": return uncheckedDivisionByZeroFunc(input);
            case "checked": return checkedIoExceptionFunc(input);
            default: return successFunc(input);
        }
    }
}
