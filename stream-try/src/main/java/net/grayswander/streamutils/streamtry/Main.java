package net.grayswander.streamutils.streamtry;

import io.vavr.control.Try;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.io.IOException;
import java.util.Arrays;

public class Main {
    public static void main(String[] args) {

        ;

        Arrays.asList("Hello", "bamba", "unchecked", "hey", "checked", "still here")
                .stream()
                .map(TryWrapper.of(ExampleProcessingFunctions::processFunc))
                .peek(resultPair -> resultPair.onFailure(Main::handleFailure))
                .filter(ResultPair::isSuccess)
                .map(ResultPair::getResult)
                .map(Try::get)
                .forEach(System.out::println);
    }



    private static void handleFailure(String s, Throwable throwable) {
        System.out.println(s + " -> " + ExceptionUtils.getStackTrace(throwable));
    }
}
