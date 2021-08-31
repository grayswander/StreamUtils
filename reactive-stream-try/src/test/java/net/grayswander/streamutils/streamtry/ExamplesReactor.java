package net.grayswander.streamutils.streamtry;

import io.vavr.control.Try;
import org.apache.commons.lang3.exception.ExceptionUtils;
import reactor.core.publisher.Flux;

import java.util.Arrays;

public class ExamplesReactor {
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
        Flux.fromIterable(STRINGS)
                .map(ReactiveTryFunction.of(TestProcessingFunctions::processFunc))
                .flatMap(ReactiveResultPair::toFlux)
                .subscribe(System.out::println);
    }

    private static void examplePrintSuccessSideEffectOnErrors() {
        System.out.println();
        System.out.println("Print successful results, side effect on failures.");
        System.out.println("--------------------------------------------");
        Flux.fromIterable(STRINGS)
                .map(ReactiveTryFunction.of(TestProcessingFunctions::processFunc))
                .flatMap(resultPair -> resultPair.toFlux(ExamplesReactor::handleFailure))
                .subscribe(System.out::println);
    }

    private static void exampleInvokeSideEffectOnFailureAndSuccess() {
        System.out.println();
        System.out.println("Invoke side effect on success and failure.");
        System.out.println("------------------------------------------");
        Flux.fromIterable(STRINGS)
                .map(ReactiveTryFunction.of(TestProcessingFunctions::processFunc))
                .doOnNext(resultPair -> resultPair.onFailure(ExamplesReactor::handleFailure))
                .doOnNext(resultPair -> resultPair.onSuccess(ExamplesReactor::handleSuccess))
                .subscribe();
    }

    private static void exampleExplicitFilterAndResultExtraction() {
        System.out.println();
        System.out.println("Explicit filter and result extraction.");
        System.out.println("--------------------------------------");
        Flux.fromIterable(STRINGS)
                .map(ReactiveTryFunction.of(TestProcessingFunctions::processFunc))
                .filter(ReactiveResultPair::isSuccess)
                .map(ReactiveResultPair::getResult)
                .map(Try::get)
                .subscribe(System.out::println);
    }


    private static void handleFailure(String s, Throwable throwable) {
        System.out.println("Side effect: " + s + " -> " + ExceptionUtils.getStackTrace(throwable));
    }

    private static void handleSuccess(String s, String r) {
        System.out.println("Side effect: " + s + " -> " + r);
    }
}
