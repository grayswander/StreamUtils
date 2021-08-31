package net.grayswander.streamutils.streamtry;

import io.vavr.control.Try;
import lombok.Value;
import lombok.experimental.NonFinal;

import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.stream.Stream;

/**
 * Intended to contain processing input with result.
 */
@Value
@NonFinal
public class ResultPair<INPUT, OUTPUT> {
    INPUT input;
    Try<OUTPUT> result;

    protected ResultPair(INPUT input, Try<OUTPUT> result) {
        this.input = input;
        this.result = result;
    }

    public static <INPUT, OUTPUT> ResultPair<INPUT, OUTPUT> of(INPUT input, Try<OUTPUT> result) {
        return new ResultPair<>(input, result);
    }

    public static <INPUT, OUTPUT> ResultPair<INPUT, OUTPUT> ofSuccess(INPUT input, OUTPUT result) {
        return new ResultPair<>(input, Try.success(result));
    }

    public static <INPUT, OUTPUT> ResultPair<INPUT, OUTPUT> ofFailure(INPUT input, Throwable throwable) {
        return new ResultPair<>(input, Try.failure(throwable));
    }

    public boolean isFailure() {
        return result.isFailure();
    }

    public boolean isSuccess() {
        return result.isSuccess();
    }

    public ResultPair<INPUT, OUTPUT> onSuccess(BiConsumer<INPUT, ? super OUTPUT> successAction) {
        Objects.requireNonNull(successAction, "action is null");
        if (isSuccess()) {
            successAction.accept(getInput(), result.get());
        }
        return this;
    }

    public ResultPair<INPUT, OUTPUT> onFailure(BiConsumer<INPUT, ? super Throwable> failureAction) {
        Objects.requireNonNull(failureAction, "action is null");
        if (isFailure()) {
            failureAction.accept(getInput(), result.getCause());
        }
        return this;
    }

    public Stream<OUTPUT> toStream() {
        return getResult().toJavaStream();
    }

    public Stream<OUTPUT> toStream(BiConsumer<INPUT, ? super Throwable> failureAction) {
        return onFailure(failureAction).getResult().toJavaStream();
    }
}
