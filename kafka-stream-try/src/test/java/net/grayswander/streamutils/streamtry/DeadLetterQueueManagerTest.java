package net.grayswander.streamutils.streamtry;

import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.*;
import org.apache.kafka.streams.kstream.KStream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Objects;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

class DeadLetterQueueManagerTest {

    private StreamsBuilder streamsBuilder;
    private KStream<String, String> inputStream;
    private KStream<String, String> outputStream;
    private Properties props;
    private TopologyTestDriver testDriver;
    private TestInputTopic<String, String> inputTopic;
    private TestOutputTopic<String, String> outputTopic;
    private TestOutputTopic<String, String> dlqTopic;
    private DeadLetterQueueManager dlqManager;
    private Serde<String> stringSerde = new Serdes.StringSerde();

    @BeforeEach
    void setUp() {
        streamsBuilder = new StreamsBuilder();
        inputStream = streamsBuilder.stream("input-topic");

        dlqManager = new DeadLetterQueueManager();

        // setup test driver
        props = new Properties();
        props.setProperty(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        props.setProperty(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());

    }

    protected void buildTestDriver() {
        outputStream.to("output-topic");
        KStream<String, DeadLetterQueueRecord<? extends KeyValue<?, ?>>> dlqKstream = dlqManager.mergeDeadLetterQueues();
        dlqKstream.mapValues(DeadLetterQueueRecord::toString).to("dlq-topic");
        testDriver = new TopologyTestDriver(streamsBuilder.build(), props);
        inputTopic = testDriver.createInputTopic("input-topic", stringSerde.serializer(), stringSerde.serializer());
        outputTopic = testDriver.createOutputTopic("output-topic", stringSerde.deserializer(), stringSerde.deserializer());
        dlqTopic = testDriver.createOutputTopic("dlq-topic", stringSerde.deserializer(), stringSerde.deserializer());
    }

    @Test
    public void testSuccessMap() {
        outputStream = dlqManager.map("Test", inputStream, (k, v) ->  KeyValue.pair(TestProcessingFunctions.successFunc(k), TestProcessingFunctions.successFunc(v)));
        buildTestDriver();
        inputTopic.pipeInput("key", "value");
        KeyValue<String, String> output = outputTopic.readKeyValue();
        assertEquals(TestProcessingFunctions.successFunc("key"), output.key);
        assertEquals(TestProcessingFunctions.successFunc("value"), output.value);
//        assertNull(dlqTopic.readKeyValue());
    }

    @Test
    public void testFailureMap() {
        outputStream = dlqManager.map("Test", inputStream, (k, v) ->  KeyValue.pair(TestProcessingFunctions.successFunc(k), TestProcessingFunctions.uncheckedDivisionByZeroFunc(v)));
        buildTestDriver();
        inputTopic.pipeInput("key", "value");
//        KeyValue<String, String> output = outputTopic.readKeyValue();
//        assertNull(output);
        KeyValue<String, String> dlq = dlqTopic.readKeyValue();
        assertNotNull(dlq);
        assertEquals("Test", dlq.key, "DLQ record key should be Test");
        assertTrue(dlq.value.contains("input=KeyValue(key, value)"), "DLQ value does not contain correct input value");
        assertTrue(dlq.value.contains("error=java.lang.ArithmeticException: / by zero"), "DLQ value does not contain correct exception");
    }

    @Test
    public void testSuccessMapValues() {
        outputStream = dlqManager.mapValues("Test", inputStream, TestProcessingFunctions::successFunc);
        buildTestDriver();
        inputTopic.pipeInput("key", "value");
        KeyValue<String, String> output = outputTopic.readKeyValue();
        assertEquals("key", output.key);
        assertEquals(TestProcessingFunctions.successFunc("value"), output.value);
//        assertNull(dlqTopic.readKeyValue());
    }

    @Test
    public void testFailureMapValues() {
        outputStream = dlqManager.mapValues("Test", inputStream, TestProcessingFunctions::uncheckedDivisionByZeroFunc);
        buildTestDriver();
        inputTopic.pipeInput("key", "value");
//        KeyValue<String, String> output = outputTopic.readKeyValue();
//        assertNull(output);
        KeyValue<String, String> dlq = dlqTopic.readKeyValue();
        assertNotNull(dlq);
        assertEquals("Test", dlq.key, "DLQ record key should be Test");
        assertTrue(dlq.value.contains("input=KeyValue(key, value)"), "DLQ value does not contain correct input value");
        assertTrue(dlq.value.contains("error=java.lang.ArithmeticException: / by zero"), "DLQ value does not contain correct exception");
    }

    @Test
    public void testSuccessMapValuesWithKey() {
        outputStream = dlqManager.mapValues("Test", inputStream, (k, v) ->  TestProcessingFunctions.successFunc(v));
        buildTestDriver();
        inputTopic.pipeInput("key", "value");
        KeyValue<String, String> output = outputTopic.readKeyValue();
        assertEquals("key", output.key);
        assertEquals(TestProcessingFunctions.successFunc("value"), output.value);
//        assertNull(dlqTopic.readKeyValue());
    }

    @Test
    public void testFailureMapValuesWithKey() {
        outputStream = dlqManager.mapValues("Test", inputStream, (k, v) ->  TestProcessingFunctions.uncheckedDivisionByZeroFunc(v));
        buildTestDriver();
        inputTopic.pipeInput("key", "value");
//        KeyValue<String, String> output = outputTopic.readKeyValue();
//        assertNull(output);
        KeyValue<String, String> dlq = dlqTopic.readKeyValue();
        System.out.println(dlq);
        assertNotNull(dlq);
        assertEquals("Test", dlq.key, "DLQ record key should be Test");
        assertTrue(dlq.value.contains("KeyValue(key, value)"), "DLQ value does not contain correct input value");
        assertTrue(dlq.value.contains("error=java.lang.ArithmeticException: / by zero"), "DLQ value does not contain correct exception");
    }

    @AfterEach
    void tearDown() {
        testDriver.close();
    }





}