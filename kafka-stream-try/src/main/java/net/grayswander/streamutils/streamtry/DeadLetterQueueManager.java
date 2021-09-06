package net.grayswander.streamutils.streamtry;

import io.vavr.CheckedFunction1;
import io.vavr.CheckedFunction2;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.Branched;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Named;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class DeadLetterQueueManager {

    private Map<String, KStream<?, DeadLetterQueueRecord<?>>> deadLetterQueues = new HashMap<>();
    private final String successDlqMessage = "Success";

    public <K, VI, VO> KStream<K, VO> branchDeadLetterQueue(String name, KStream<K, ResultPair<VI, VO>> kstream) {
        Map<String, KStream<K, ResultPair<VI, VO>>> branches =
                kstream.split(Named.as(name))
                .branch((key, value) -> value.isFailure(), Branched.as("Failure"))
                .defaultBranch(Branched.as("Success"));

        KStream<K, ResultPair<VI, VO>> failedStream = branches.get(name + "Failure");
        deadLetterQueues.put(name, failedStream.mapValues(this::buildDlqRecord));

        return branches.get(name+"Success").mapValues(value -> value.getResult().get());

    }

    protected <T> DeadLetterQueueRecord<T> buildDlqRecord(ResultPair<T, ?> resultPair) {
        return DeadLetterQueueRecord.of(
                resultPair.getInput(),
                resultPair.isFailure()
                        ? this.convertThrowableForDlq(resultPair.getResult().getCause())
                        : successDlqMessage
                );
    }

    protected String convertThrowableForDlq(Throwable throwable) {
        return ExceptionUtils.getStackTrace(throwable);
    }

    public KStream<String, DeadLetterQueueRecord<? extends KeyValue<?, ?>>> mergeDeadLetterQueues() {
        return mergeDeadLetterQueues(this.deadLetterQueues.keySet());
    }

    public KStream<String, DeadLetterQueueRecord<? extends KeyValue<?, ?>>> mergeDeadLetterQueues(Collection<String> queues) {
        Optional<KStream<String, DeadLetterQueueRecord<? extends KeyValue<?, ?>>>> reduce = this.deadLetterQueues.entrySet().stream()
                .filter(entry -> queues.contains(entry.getKey()))
                .map(stringKStreamEntry -> {
                    String key = stringKStreamEntry.getKey();
                    KStream<?, DeadLetterQueueRecord<?>> stream = stringKStreamEntry.getValue();
                    KStream<String, DeadLetterQueueRecord<? extends KeyValue<?, ?>>> kStream = stream.map((key1, value) -> KeyValue.pair(key, DeadLetterQueueRecord.of(KeyValue.pair(key1, value.getInput()), value.getError())));
                    return kStream;
                })
                .reduce(KStream::merge);
        return reduce.get();
    }

    public <K, VI, VO> KStream<K, VO> mapValues(String name, KStream<K, VI> kstream, CheckedFunction1<VI, VO> function) {
        return this.branchDeadLetterQueue(name, kstream.mapValues(KStreamTryValueMapper.of(function)));
    }

    public <K, VI, VO> KStream<K, VO> mapValues(String name, KStream<K, VI> kstream, CheckedFunction2<K, VI, VO> function) {
        return this.branchDeadLetterQueue(name, kstream.mapValues(KStreamTryValueMapperWithKey.of(function)));
    }

    public <K, VI, KO, VO> KStream<KO, VO> map(String name, KStream<K, VI> kstream, CheckedFunction2<K, VI, KeyValue<? extends KO, ? extends VO>> function) {
        return this.branchDeadLetterQueue(name, kstream.map(KStreamTryKeyValueMapper.of(function))).map((key, value) -> value);
    }

    public Map<String, KStream<?, DeadLetterQueueRecord<?>>> getDeadLetterQueues() {
        return deadLetterQueues;
    }

}