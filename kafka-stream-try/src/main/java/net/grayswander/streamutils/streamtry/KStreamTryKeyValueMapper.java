package net.grayswander.streamutils.streamtry;

import io.vavr.CheckedFunction2;
import io.vavr.control.Try;
import lombok.AllArgsConstructor;
import lombok.Value;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.KeyValueMapper;

@Value
@AllArgsConstructor
public class KStreamTryKeyValueMapper<KEY, VALUE, KOUT, VOUT> implements KeyValueMapper<KEY, VALUE, KeyValue<String, ResultPair<KeyValue<KEY, VALUE>, KeyValue<? extends KOUT, ? extends VOUT>>>> {

    CheckedFunction2<KEY, VALUE, KeyValue<? extends KOUT, ? extends VOUT>> function;

    public static <KEY, VALUE, KOUT, VOUT> KStreamTryKeyValueMapper<KEY, VALUE, KOUT, VOUT> of(CheckedFunction2<KEY, VALUE, KeyValue<? extends KOUT, ? extends VOUT>> function) {
        return new KStreamTryKeyValueMapper<>(function);
    }

    @Override
    public KeyValue<String,ResultPair<KeyValue<KEY, VALUE>, KeyValue<? extends KOUT, ? extends VOUT>>> apply(KEY key, VALUE value) {
        return KeyValue.pair("DUMMY", ResultPair.of(KeyValue.pair(key, value), Try.of(() -> function.apply(key, value))));
    }
}
