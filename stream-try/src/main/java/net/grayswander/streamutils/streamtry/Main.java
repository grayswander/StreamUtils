package net.grayswander.streamutils.streamtry;

import io.vavr.control.Try;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.util.Arrays;

public class Main {
    public static void main(String[] args) {

        ;

        Arrays.asList("Hello", "bamba", "unchecked", "hey", "checked", "still here")
                .stream()
                .map(TryFunction.of(ExampleProcessingFunctions::processFunc))
//                .peek(resultPair -> resultPair.onFailure(Main::handleFailure))
//                .filter(ResultPair::isSuccess)
                .flatMap(ResultPair::toStream)
                .forEach(System.out::println);

    }



    private static void handleFailure(String s, Throwable throwable) {
        System.out.println(s + " -> " + ExceptionUtils.getStackTrace(throwable));
    }
}
