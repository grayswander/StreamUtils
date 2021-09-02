package net.grayswander.streamutils.streamtry;

import io.vavr.CheckedFunction1;
import io.vavr.control.Try;
import lombok.AllArgsConstructor;
import lombok.Value;
import org.apache.kafka.streams.kstream.ValueMapper;

@Value
@AllArgsConstructor
public class KStreamTryValueMapper<INPUT, OUTPUT> implements ValueMapper<INPUT, ResultPair<INPUT, OUTPUT>> {

    CheckedFunction1<INPUT, ? extends OUTPUT> function;

    @Override
    public ResultPair<INPUT, OUTPUT> apply(INPUT input) {
        return ResultPair.of(input, Try.of(() -> function.apply(input)));
    }

    public static <INPUT, OUTPUT> KStreamTryValueMapper<INPUT, OUTPUT> of(CheckedFunction1<INPUT, ? extends OUTPUT> function) {
        return new KStreamTryValueMapper<>(function);
    }


}
