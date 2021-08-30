package net.grayswander.streamutils.streamtry;

import io.vavr.control.Try;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class ResultPairTest {

    String input = "input";
    String output = "output";
    Throwable throwable = new Exception("Example Exception");

    Try<String> trySuccess = Try.success(output);
    Try<String> tryFailure = Try.failure(throwable);

    String flagInput = "flag";
    String flagOutput = "flag";
    Throwable flagThrowable = new Exception("flag");

    public void setFlagInput(String flagInput) {
        this.flagInput = flagInput;
    }

    public void setFlagOutput(String flagOutput) {
        this.flagOutput = flagOutput;
    }

    public void setFlagThrowable(Throwable flagThrowable) {
        this.flagThrowable = flagThrowable;
    }

    @BeforeEach
    void setUp() {
    }

    @Test
    void of() {
        ResultPair<String, String> resultPair = ResultPair.of(input, trySuccess);
        assertEquals(input, resultPair.getInput());
        assertEquals(trySuccess, resultPair.getResult());
    }

    @Test
    void ofSuccess() {
        ResultPair<String, String> resultPair = ResultPair.ofSuccess(input, output);
        assertEquals(input, resultPair.getInput());
        assertEquals(output, resultPair.getResult().get());
        assertTrue(resultPair.isSuccess());
        assertFalse(resultPair.isFailure());
    }

    @Test
    void ofFailure() {
        ResultPair<String, String> resultPair = ResultPair.ofFailure(input, throwable);
        assertEquals(input, resultPair.getInput());
        assertEquals(throwable, resultPair.getResult().getCause());
        assertTrue(resultPair.isFailure());
        assertFalse(resultPair.isSuccess());
    }

    @Test
    void isFailureWhenFailureShouldBeTrue() {
        ResultPair<String, String> resultPair = ResultPair.of(input, tryFailure);
        assertTrue(resultPair.isFailure());
    }
    @Test
    void isFailureWhenSuccessShouldBeFalse() {
        ResultPair<String, String> resultPair = ResultPair.of(input, trySuccess);
        assertFalse(resultPair.isFailure());
    }

    @Test
    void isSuccessWhenSuccessShouldBeTrue() {
        ResultPair<String, String> resultPair = ResultPair.of(input, trySuccess);
        assertTrue(resultPair.isSuccess());
    }
    @Test
    void isSuccessWhenFailureShouldBeFalse() {
        ResultPair<String, String> resultPair = ResultPair.of(input, tryFailure);
        assertFalse(resultPair.isSuccess());
    }

    @Test
    void onSuccessWhenSuccess() {
        ResultPair<String, String> resultPair = ResultPair.of(input, trySuccess);

        resultPair.onSuccess((s, s2) -> {
            setFlagInput(s);
            setFlagOutput(s2);
        });
        assertEquals(flagInput, input);
        assertEquals(flagOutput, output);
    }
    @Test
    void onSuccessWhenFailure() {
        ResultPair<String, String> resultPair = ResultPair.of(input, tryFailure);
        resultPair.onSuccess((s, s2) -> {
            setFlagInput(s);
            setFlagOutput(s2);
        });
        assertNotEquals(flagInput, input);
        assertNotEquals(flagOutput, output);
    }

    @Test
    void onFailureWhenSuccess() {
        ResultPair<String, String> resultPair = ResultPair.of(input, trySuccess);
       resultPair.onFailure((s, throwable1) -> {
           setFlagInput(s);
           setFlagThrowable(throwable1);
       });
        assertNotEquals(flagInput, input);
        assertNotEquals(flagThrowable, throwable);
    }

    @Test
    void onFailureWhenFailure() {
        ResultPair<String, String> resultPair = ResultPair.of(input, tryFailure);
        resultPair.onFailure((s, throwable1) -> {
            setFlagInput(s);
            setFlagThrowable(throwable1);
        });
        assertEquals(flagInput, input);
        assertEquals(flagThrowable, throwable);
    }

    @Test
    void getInput() {
        ResultPair<String, String> resultPair = ResultPair.of(input, trySuccess);
        assertEquals(input, resultPair.getInput());
    }

    @Test
    void getResult() {
        ResultPair<String, String> resultPair = ResultPair.of(input, trySuccess);
        assertEquals(trySuccess, resultPair.getResult());
    }

    @Test
    void testLombokValueAnnotation() {
        ResultPair<String, String> resultPair1 = ResultPair.of(input, trySuccess);
        ResultPair<String, String> resultPair2 = ResultPair.of(input, tryFailure);
        ResultPair<String, String> resultPair3 = ResultPair.of(input, trySuccess);

        assertEquals(resultPair1.hashCode(), resultPair3.hashCode());
        assertEquals(resultPair1, resultPair3);
        assertNotEquals(resultPair1, resultPair2);
        assertTrue(resultPair1.toString().contains(input));
    }

    @Test
    void toStreamWhenSuccess() {
        ResultPair<String, String> resultPair = ResultPair.of(input, trySuccess);
        List<String> list = resultPair.toStream().collect(Collectors.toList());
        assertEquals(1, list.size());
        assertEquals(output, list.get(0));
    }

    @Test
    void toStreamWhenFailure() {
        ResultPair<String, String> resultPair = ResultPair.of(input, tryFailure);
        List<String> list = resultPair.toStream().collect(Collectors.toList());
        assertEquals(0, list.size());
    }
}