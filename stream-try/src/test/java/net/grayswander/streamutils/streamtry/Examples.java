package net.grayswander.streamutils.streamtry;

import io.vavr.control.Try;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.util.Arrays;
import java.util.stream.Collectors;

public class Examples {
    // Given test processing function:
    // "unchecked" will throw unchecked exception
    // "checked" will throw checked exception
    // Other values will return "Success: <value>"
    public static final java.util.List<String> STRINGS =
            Arrays.asList("Hello", "bamba", "unchecked", "hey", "checked", "still here");

    public static void main(String[] args) {
        examplePrintSuccessIgnoreErrors();
        examplePrintSuccessSideEffectOnErrors();
        exampleInvokeSideEffectOnFailureAndSuccess();
        exampleExplicitFilterAndResultExtraction();
    }

    private static void examplePrintSuccessIgnoreErrors() {
        System.out.println();
        System.out.println("Print successful results, ignoring failures.");
        System.out.println("--------------------------------------------");
        STRINGS
                .stream()
                .map(TryFunction.of(TestProcessingFunctions::processFunc))
                .flatMap(ResultPair::toStream)
                .forEach(System.out::println);
    }

    private static void examplePrintSuccessSideEffectOnErrors() {
        System.out.println();
        System.out.println("Print successful results, side effect on failures.");
        System.out.println("--------------------------------------------");
        STRINGS
                .stream()
                .map(TryFunction.of(TestProcessingFunctions::processFunc))
                .flatMap(resultPair -> resultPair.toStream(Examples::handleFailure))
                .forEach(System.out::println);
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private static void exampleInvokeSideEffectOnFailureAndSuccess() {
        System.out.println();
        System.out.println("Invoke side effect on success and failure.");
        System.out.println("------------------------------------------");
        STRINGS
                .stream()
                .map(TryFunction.of(TestProcessingFunctions::processFunc))
                .peek(resultPair -> resultPair.onFailure(Examples::handleFailure))
                .peek(resultPair -> resultPair.onSuccess(Examples::handleSuccess))
                .collect(Collectors.toList());
    }

    private static void exampleExplicitFilterAndResultExtraction() {
        System.out.println();
        System.out.println("Explicit filter and result extraction.");
        System.out.println("--------------------------------------");
        STRINGS
                .stream()
                .map(TryFunction.of(TestProcessingFunctions::processFunc))
                .filter(ResultPair::isSuccess)
                .map(ResultPair::getResult)
                .map(Try::get)
                .forEach(System.out::println);
    }


    private static void handleFailure(String s, Throwable throwable) {
        System.out.println("Side effect: " + s + " -> " + ExceptionUtils.getStackTrace(throwable));
    }

    private static void handleSuccess(String s, String r) {
        System.out.println("Side effect: " + s + " -> " + r);
    }
}
