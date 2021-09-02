package net.grayswander.streamutils.streamtry;

import io.vavr.CheckedFunction2;
import io.vavr.control.Try;
import lombok.AllArgsConstructor;
import lombok.Value;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.KeyValueMapper;

@Value
@AllArgsConstructor
public class KStreamTryKeyValueMapper<KEY, VALUE, OUTPUT> implements KeyValueMapper<KEY, VALUE, ResultPair<KeyValue<KEY, VALUE>, OUTPUT>> {

    CheckedFunction2<KEY, VALUE, ? extends OUTPUT> function;

    public static <KEY, VALUE, OUTPUT> KStreamTryKeyValueMapper<KEY, VALUE, OUTPUT> of(CheckedFunction2<KEY, VALUE, ? extends OUTPUT> function) {
        return new KStreamTryKeyValueMapper<>(function);
    }

    @Override
    public ResultPair<KeyValue<KEY, VALUE>, OUTPUT> apply(KEY key, VALUE value) {
        return ResultPair.of(KeyValue.pair(key, value), Try.of(() -> function.apply(key, value)));
    }
}
