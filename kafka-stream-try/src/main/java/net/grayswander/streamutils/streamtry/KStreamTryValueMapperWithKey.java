package net.grayswander.streamutils.streamtry;

import io.vavr.CheckedFunction2;
import io.vavr.control.Try;
import lombok.AllArgsConstructor;
import lombok.Value;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.ValueMapperWithKey;

@Value
@AllArgsConstructor
public class KStreamTryValueMapperWithKey<KEY, VALUE, OUTPUT> implements ValueMapperWithKey<KEY, VALUE, ResultPair<KeyValue<KEY, VALUE>, OUTPUT>> {

    CheckedFunction2<KEY, VALUE, ? extends OUTPUT> function;

    public static <KEY, VALUE, OUTPUT> KStreamTryValueMapperWithKey<KEY, VALUE, OUTPUT> of(CheckedFunction2<KEY, VALUE, ? extends OUTPUT> function) {
        return new KStreamTryValueMapperWithKey<>(function);
    }

    @Override
    public ResultPair<KeyValue<KEY, VALUE>, OUTPUT> apply(KEY readOnlyKey, VALUE value) {
        return ResultPair.of(KeyValue.pair(readOnlyKey, value), Try.of(() -> function.apply(readOnlyKey, value)));
    }
}
