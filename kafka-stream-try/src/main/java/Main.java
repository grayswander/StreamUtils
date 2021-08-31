import net.grayswander.streamutils.streamtry.ResultPair;
import net.grayswander.streamutils.streamtry.KstreamTryFunction;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.*;
import org.apache.kafka.streams.kstream.Branched;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Named;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
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

        KstreamTryFunction<String, String> kstreamTryFunction = KstreamTryFunction.of(TestProcessingFunctions::processFunc);

        Map<String, KStream<String, ResultPair<String, String>>> stringKStreamMap = textLines
                .map((key, value) -> KeyValue.pair(key, value))
                .mapValues(value -> kstreamTryFunction.apply(value))
                .split(Named.as("Processing"))
                .branch((key, value) -> value.isFailure(), Branched.as("Failed"))
//                .branch((key, value) -> value.isSuccess(), Branched.as("Succeeded"))
                .defaultBranch(Branched.as("Succeeded"));

        stringKStreamMap.get("ProcessingSucceeded")
                .mapValues(value -> value
                        .getResult()
                        .get())
                .foreach((key, value) -> System.out.println(value));

        stringKStreamMap.get("ProcessingFailed")
                .mapValues(value -> "Error: " + value.getInput()+ " -> " + value.getResult().getCause().getMessage())
                .foreach((key, value) -> System.out.println(value));

        Topology topology = builder.build();
        KafkaStreams streams = new KafkaStreams(topology, streamsConfiguration);
        streams.start();

        Thread.sleep(30000);
        streams.close();

    }
}
