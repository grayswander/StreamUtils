package net.grayswander.streamutils.streamtry;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TryFunctionTest {
    private final String input = "input";

    @Test
    void testSuccess() {
        TryFunction<String, String> tryFunction = TryFunction.of(TestProcessingFunctions::successFunc);
        ResultPair<String, String> resultPair = tryFunction.apply(input);
        assertEquals(input, resultPair.getInput());
        assertEquals(TestProcessingFunctions.successFunc(input), resultPair.getResult().get());
    }

    @Test
    void testCheckedException() {
        TryFunction<String, String> tryFunction = TryFunction.of(TestProcessingFunctions::checkedIoExceptionFunc);
        ResultPair<String, String> resultPair = tryFunction.apply(input);
        assertEquals(input, resultPair.getInput());
        try {
            TestProcessingFunctions.checkedIoExceptionFunc(input);
        } catch (Exception e) {
            assertEquals(e.getMessage(), resultPair.getResult().getCause().getMessage());
            assertEquals(e.getClass(), resultPair.getResult().getCause().getClass());
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test
    void testUncheckedException() {
        TryFunction<String, String> tryFunction = TryFunction.of(TestProcessingFunctions::uncheckedDivisionByZeroFunc);
        ResultPair<String, String> resultPair = tryFunction.apply(input);
        assertEquals(input, resultPair.getInput());
        try {
            TestProcessingFunctions.uncheckedDivisionByZeroFunc(input);
        } catch (Exception e) {
            assertEquals(e.getMessage(), resultPair.getResult().getCause().getMessage());
            assertEquals(e.getClass(), resultPair.getResult().getCause().getClass());
        }
    }

    @Test
    void testLombokValue() throws Throwable {
        TryFunction<String, String> tryFunction = TryFunction.of(TestProcessingFunctions::successFunc);
        assertEquals(TestProcessingFunctions.successFunc(input), tryFunction.getFunction().apply(input));
        assertTrue(tryFunction.toString().length() > 0);
    }
}