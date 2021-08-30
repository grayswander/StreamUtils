package net.grayswander.streamutils.streamtry;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TryFunctionTest {
    private final String input = "input";

    @Test
    void testSuccess() {
        TryFunction<String, String> tryFunction = TryFunction.of(ExampleProcessingFunctions::successFunc);
        ResultPair<String, String> resultPair = tryFunction.apply(input);
        assertEquals(input, resultPair.getInput());
        assertEquals(ExampleProcessingFunctions.successFunc(input), resultPair.getResult().get());
    }

    @Test
    void testCheckedException() {
        TryFunction<String, String> tryFunction = TryFunction.of(ExampleProcessingFunctions::checkedIoExceptionFunc);
        ResultPair<String, String> resultPair = tryFunction.apply(input);
        assertEquals(input, resultPair.getInput());
        try {
            ExampleProcessingFunctions.checkedIoExceptionFunc(input);
        } catch (Exception e) {
            assertEquals(e.getMessage(), resultPair.getResult().getCause().getMessage());
            assertEquals(e.getClass(), resultPair.getResult().getCause().getClass());
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test
    void testUncheckedException() {
        TryFunction<String, String> tryFunction = TryFunction.of(ExampleProcessingFunctions::uncheckedDivisionByZeroFunc);
        ResultPair<String, String> resultPair = tryFunction.apply(input);
        assertEquals(input, resultPair.getInput());
        try {
            ExampleProcessingFunctions.uncheckedDivisionByZeroFunc(input);
        } catch (Exception e) {
            assertEquals(e.getMessage(), resultPair.getResult().getCause().getMessage());
            assertEquals(e.getClass(), resultPair.getResult().getCause().getClass());
        }
    }

    @Test
    void testLombokValue() throws Throwable {
        TryFunction<String, String> tryFunction = TryFunction.of(ExampleProcessingFunctions::successFunc);
        assertEquals(ExampleProcessingFunctions.successFunc(input), tryFunction.getFunction().apply(input));
        assertTrue(tryFunction.toString().length() > 0);
    }
}