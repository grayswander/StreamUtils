package net.grayswander.streamutils.streamtry;

import org.apache.kafka.streams.KeyValue;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class KStreamTryValueMapperWithKeyTest {

    @Test
    void apply() {
        KStreamTryValueMapperWithKey<String, String, String> keyValueMapper = KStreamTryValueMapperWithKey.of((String o, String o2) -> TestProcessingFunctions.successFunc(o2));

        String key = "key";
        String value = "value";
        ResultPair<KeyValue<String, String>, String> resultPair = keyValueMapper.apply(key, value);

        KeyValue<String, String> input = resultPair.getInput();
        String output = resultPair.getResult().get();

        assertEquals(key, input.key);
        assertEquals(value, input.value);
        assertEquals(TestProcessingFunctions.successFunc(value), output);
    }
}