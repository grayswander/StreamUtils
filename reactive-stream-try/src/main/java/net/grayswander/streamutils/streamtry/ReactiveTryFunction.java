package net.grayswander.streamutils.streamtry;

import io.vavr.CheckedFunction1;
import io.vavr.control.Try;
import lombok.AllArgsConstructor;
import lombok.Value;

import java.util.function.Function;

@Value
@AllArgsConstructor
public class ReactiveTryFunction<INPUT, OUTPUT> implements Function<INPUT, ReactiveResultPair<INPUT, OUTPUT>> {

    CheckedFunction1<INPUT, ? extends OUTPUT> function;

    @Override
    public ReactiveResultPair<INPUT, OUTPUT> apply(INPUT input) {
        return ReactiveResultPair.of(input, Try.of(() -> function.apply(input)));
    }

    public static <INPUT, OUTPUT> ReactiveTryFunction<INPUT, OUTPUT> of(CheckedFunction1<INPUT, ? extends OUTPUT> function) {
        return new ReactiveTryFunction<>(function);
    }
}
