package net.grayswander.streamutils.streamtry;

import org.apache.kafka.streams.KeyValue;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class KStreamTryKeyValueMapperTest {

    @Test
    void apply() {
        KStreamTryKeyValueMapper<String, String, String, String> keyValueMapper = KStreamTryKeyValueMapper.of((String o, String o2) -> KeyValue.pair(TestProcessingFunctions.successFunc(o), TestProcessingFunctions.successFunc(o2)));

        String key = "key";
        String value = "value";
        KeyValue<String, ResultPair<KeyValue<String, String>, KeyValue<? extends String, ? extends String>>> keyValue = keyValueMapper.apply(key, value);

        KeyValue<String, String> input = keyValue.value.getInput();
        KeyValue<? extends String, ? extends String> output = keyValue.value.getResult().get();
        assertEquals(TestProcessingFunctions.successFunc(key), keyValue.key);
        assertEquals(key, input.key);
        assertEquals(value, input.value);
        assertEquals(TestProcessingFunctions.successFunc(key), output.key);
        assertEquals(TestProcessingFunctions.successFunc(value), output.value);
    }
}