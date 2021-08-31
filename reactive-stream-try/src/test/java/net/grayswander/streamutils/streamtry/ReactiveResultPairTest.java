package net.grayswander.streamutils.streamtry;

import io.vavr.control.Try;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class ReactiveResultPairTest {

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
        ReactiveResultPair<String, String> resultPair = ReactiveResultPair.of(input, trySuccess);
        assertEquals(input, resultPair.getInput());
        assertEquals(trySuccess, resultPair.getResult());
    }

    @Test
    void ofSuccess() {
        ReactiveResultPair<String, String> resultPair = ReactiveResultPair.ofSuccess(input, output);
        assertEquals(input, resultPair.getInput());
        assertEquals(output, resultPair.getResult().get());
        assertTrue(resultPair.isSuccess());
        assertFalse(resultPair.isFailure());
    }

    @Test
    void ofFailure() {
        ReactiveResultPair<String, String> resultPair = ReactiveResultPair.ofFailure(input, throwable);
        assertEquals(input, resultPair.getInput());
        assertEquals(throwable, resultPair.getResult().getCause());
        assertTrue(resultPair.isFailure());
        assertFalse(resultPair.isSuccess());
    }

    @Test
    void isFailureWhenFailureShouldBeTrue() {
        ReactiveResultPair<String, String> resultPair = ReactiveResultPair.of(input, tryFailure);
        assertTrue(resultPair.isFailure());
    }
    @Test
    void isFailureWhenSuccessShouldBeFalse() {
        ReactiveResultPair<String, String> resultPair = ReactiveResultPair.of(input, trySuccess);
        assertFalse(resultPair.isFailure());
    }

    @Test
    void isSuccessWhenSuccessShouldBeTrue() {
        ReactiveResultPair<String, String> resultPair = ReactiveResultPair.of(input, trySuccess);
        assertTrue(resultPair.isSuccess());
    }
    @Test
    void isSuccessWhenFailureShouldBeFalse() {
        ReactiveResultPair<String, String> resultPair = ReactiveResultPair.of(input, tryFailure);
        assertFalse(resultPair.isSuccess());
    }

    @Test
    void onSuccessWhenSuccess() {
        ReactiveResultPair<String, String> resultPair = ReactiveResultPair.of(input, trySuccess);

        resultPair.onSuccess((s, s2) -> {
            setFlagInput(s);
            setFlagOutput(s2);
        });
        assertEquals(flagInput, input);
        assertEquals(flagOutput, output);
    }
    @Test
    void onSuccessWhenFailure() {
        ReactiveResultPair<String, String> resultPair = ReactiveResultPair.of(input, tryFailure);
        resultPair.onSuccess((s, s2) -> {
            setFlagInput(s);
            setFlagOutput(s2);
        });
        assertNotEquals(flagInput, input);
        assertNotEquals(flagOutput, output);
    }

    @Test
    void onFailureWhenSuccess() {
        ReactiveResultPair<String, String> resultPair = ReactiveResultPair.of(input, trySuccess);
       resultPair.onFailure((s, throwable1) -> {
           setFlagInput(s);
           setFlagThrowable(throwable1);
       });
        assertNotEquals(flagInput, input);
        assertNotEquals(flagThrowable, throwable);
    }

    @Test
    void onFailureWhenFailure() {
        ReactiveResultPair<String, String> resultPair = ReactiveResultPair.of(input, tryFailure);
        resultPair.onFailure((s, throwable1) -> {
            setFlagInput(s);
            setFlagThrowable(throwable1);
        });
        assertEquals(flagInput, input);
        assertEquals(flagThrowable, throwable);
    }

    @Test
    void getInput() {
        ReactiveResultPair<String, String> resultPair = ReactiveResultPair.of(input, trySuccess);
        assertEquals(input, resultPair.getInput());
    }

    @Test
    void getResult() {
        ReactiveResultPair<String, String> resultPair = ReactiveResultPair.of(input, trySuccess);
        assertEquals(trySuccess, resultPair.getResult());
    }

    @Test
    void testLombokValueAnnotation() {
        ReactiveResultPair<String, String> resultPair1 = ReactiveResultPair.of(input, trySuccess);
        ReactiveResultPair<String, String> resultPair2 = ReactiveResultPair.of(input, tryFailure);
        ReactiveResultPair<String, String> resultPair3 = ReactiveResultPair.of(input, trySuccess);

        assertEquals(resultPair1.hashCode(), resultPair3.hashCode());
        assertEquals(resultPair1, resultPair3);
        assertNotEquals(resultPair1, resultPair2);
        assertTrue(resultPair1.toString().contains(input));
    }

    @Test
    void toFluxWhenSuccess() {
        ReactiveResultPair<String, String> resultPair = ReactiveResultPair.of(input, trySuccess);
        List<String> list = new ArrayList<>();
        resultPair.toFlux().subscribe(list::add);
        assertEquals(1, list.size());
        assertEquals(output, list.get(0));
    }

    @Test
    void toStreamWhenFailure() {
        ReactiveResultPair<String, String> resultPair = ReactiveResultPair.of(input, tryFailure);
        List<String> list = new ArrayList<>();
        resultPair.toFlux().subscribe(list::add);
        assertEquals(0, list.size());
    }

    @Test
    void toStreamWithFailureActionWhenSuccess() {
        ReactiveResultPair<String, String> resultPair = ReactiveResultPair.of(input, trySuccess);
        List<String> list = new ArrayList<>();
        resultPair.toFlux((s, throwable1) -> {
            setFlagInput(s);
            setFlagThrowable(throwable1);
        }).subscribe(list::add);

        assertEquals(1, list.size());
        assertEquals(output, list.get(0));
        assertNotEquals(flagInput, input);
        assertNotEquals(flagThrowable, throwable);

    }

    @Test
    void toStreamWithFailureActionWhenFailure() {
        ReactiveResultPair<String, String> resultPair = ReactiveResultPair.of(input, tryFailure);
        List<String> list = new ArrayList<>();
        resultPair.toFlux((s, throwable1) -> {
            setFlagInput(s);
            setFlagThrowable(throwable1);
        }).subscribe(list::add);
        assertEquals(0, list.size());
        assertEquals(flagInput, input);
        assertEquals(flagThrowable, throwable);
    }
}