package net.grayswander.streamutils.streamtry;

import lombok.Value;

@Value
public class DeadLetterQueueRecord<T> {
    T input;
    String error;

    public static <T> DeadLetterQueueRecord<T> of(T input, String error) {
        return new DeadLetterQueueRecord<>(input, error);
    }
}
