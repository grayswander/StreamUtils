package net.grayswander.streamutils.streamtry;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ReactiveTryFunctionTest {
    private final String input = "input";

    @Test
    void testSuccess() {
        ReactiveTryFunction<String, String> tryFunction = ReactiveTryFunction.of(TestProcessingFunctions::successFunc);
        ReactiveResultPair<String, String> resultPair = tryFunction.apply(input);
        assertEquals(input, resultPair.getInput());
        assertEquals(TestProcessingFunctions.successFunc(input), resultPair.getResult().get());
    }

    @Test
    void testCheckedException() {
        ReactiveTryFunction<String, String> tryFunction = ReactiveTryFunction.of(TestProcessingFunctions::checkedIoExceptionFunc);
        ReactiveResultPair<String, String> resultPair = tryFunction.apply(input);
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
        ReactiveTryFunction<String, String> tryFunction = ReactiveTryFunction.of(TestProcessingFunctions::uncheckedDivisionByZeroFunc);
        ReactiveResultPair<String, String> resultPair = tryFunction.apply(input);
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
        ReactiveTryFunction<String, String> tryFunction = ReactiveTryFunction.of(TestProcessingFunctions::successFunc);
        assertEquals(TestProcessingFunctions.successFunc(input), tryFunction.getFunction().apply(input));
        assertTrue(tryFunction.toString().length() > 0);
    }
}