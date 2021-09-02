import net.grayswander.streamutils.streamtry.DeadLetterQueueManager;
import net.grayswander.streamutils.streamtry.KStreamTryValueMapper;
import net.grayswander.streamutils.streamtry.ResultPair;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.*;
import org.apache.kafka.streams.kstream.KStream;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;
import java.util.regex.Pattern;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {

        String inputTopic = "inputTopic";
        Properties streamsConfiguration = new Properties();
        streamsConfiguration.put(
                StreamsConfig.APPLICATION_ID_CONFIG,
                "kafka-stream-try");
        String bootstrapServers = "localhost:29092";
        streamsConfiguration.put(
                StreamsConfig.BOOTSTRAP_SERVERS_CONFIG,
                bootstrapServers);

        streamsConfiguration.put(
                StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG,
                Serdes.String().getClass().getName());
        streamsConfiguration.put(
                StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG,
                Serdes.String().getClass().getName());
        Path stateDirectory = Files.createTempDirectory("kafka-streams");
        streamsConfiguration.put(
                StreamsConfig.STATE_DIR_CONFIG, stateDirectory.toAbsolutePath().toString());

        StreamsBuilder builder = new StreamsBuilder();
        KStream<String, String> textLines = builder.stream(inputTopic);
        Pattern pattern = Pattern.compile("\\W+", Pattern.UNICODE_CHARACTER_CLASS);

        KStreamTryValueMapper<String, String> kStreamTryValueMapper = KStreamTryValueMapper.of(TestProcessingFunctions::processFunc);

        KStream<String, ResultPair<String, String>> kStream = textLines
                .mapValues(kStreamTryValueMapper);

        DeadLetterQueueManager dlqManager = new DeadLetterQueueManager();

        KStream<String, String> processed = dlqManager.branchDlq(kStream, "TryOne");

        processed.foreach((key, value) -> System.out.println("Processed: " + value));

        dlqManager.mergeDeadLetterQueues().foreach((key, value) -> System.out.println("Failed: " + key + " -> " + value));


        Topology topology = builder.build();
        KafkaStreams streams = new KafkaStreams(topology, streamsConfiguration);
        streams.start();

        Thread.sleep(30000);
        streams.close();

    }
}
