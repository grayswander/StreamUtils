package net.grayswander.streamutils.streamtry;

import io.vavr.CheckedFunction1;
import io.vavr.control.Try;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Value;

import java.util.function.Function;

@Value
@AllArgsConstructor
public class TryFunction<INPUT, OUTPUT> implements Function<INPUT, ResultPair<INPUT, OUTPUT>> {

    CheckedFunction1<INPUT, ? extends OUTPUT> function;

    @Override
    public ResultPair<INPUT, OUTPUT> apply(INPUT input) {
        return ResultPair.of(input, Try.of(() -> function.apply(input)));
    }

    public static <INPUT, OUTPUT> TryFunction<INPUT, OUTPUT> of(CheckedFunction1<INPUT, ? extends OUTPUT> function) {
        return new TryFunction<INPUT, OUTPUT>(function);
    }
}
