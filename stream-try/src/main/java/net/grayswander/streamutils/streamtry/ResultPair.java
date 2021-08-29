package net.grayswander.streamutils.streamtry;

import io.vavr.control.Try;
import lombok.Value;

import java.util.Objects;
import java.util.function.BiConsumer;

/**
 * Intended to contain processing input with result.
 */
@Value
public class ResultPair<INPUT, OUTPUT> {
    INPUT input;
    Try<OUTPUT> result;

    private ResultPair(INPUT input, Try<OUTPUT> result) {
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

    public ResultPair<INPUT, OUTPUT> onSuccess(BiConsumer<INPUT, ? super OUTPUT> action) {
        Objects.requireNonNull(action, "action is null");
        if (isSuccess()) {
            action.accept(getInput(), result.get());
        }
        return this;
    }

    public ResultPair<INPUT, OUTPUT> onFailure(BiConsumer<INPUT, ? super Throwable> action) {
        Objects.requireNonNull(action, "action is null");
        if (isFailure()) {
            action.accept(getInput(), result.getCause());
        }
        return this;
    }
}
