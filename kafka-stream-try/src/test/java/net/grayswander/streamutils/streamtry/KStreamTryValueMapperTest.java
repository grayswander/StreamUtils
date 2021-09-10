package net.grayswander.streamutils.streamtry;

import org.apache.kafka.streams.KeyValue;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class KStreamTryValueMapperTest {

    @Test
    void apply() {
        KStreamTryValueMapper<String, String> keyValueMapper = KStreamTryValueMapper.of(TestProcessingFunctions::successFunc);

        String value = "value";
        ResultPair<String, String> resultPair = keyValueMapper.apply(value);

        String input = resultPair.getInput();
        String output = resultPair.getResult().get();
        assertEquals(value, input);
        assertEquals(TestProcessingFunctions.successFunc(value), output);
    }
}