package net.grayswander.streamutils.streamtry;

import io.vavr.control.Try;
import reactor.core.publisher.Flux;

import java.util.function.BiConsumer;

public class ReactiveResultPair<INPUT, OUTPUT> extends ResultPair<INPUT, OUTPUT>{

    protected ReactiveResultPair(INPUT input, Try<OUTPUT> result) {
        super(input, result);
    }

    public static <INPUT, OUTPUT> ReactiveResultPair<INPUT, OUTPUT> of(INPUT input, Try<OUTPUT> result) {
        return new ReactiveResultPair<>(input, result);
    }

    public static <INPUT, OUTPUT> ReactiveResultPair<INPUT, OUTPUT> ofSuccess(INPUT input, OUTPUT result) {
        return new ReactiveResultPair<>(input, Try.success(result));
    }

    public static <INPUT, OUTPUT> ReactiveResultPair<INPUT, OUTPUT> ofFailure(INPUT input, Throwable throwable) {
        return new ReactiveResultPair<>(input, Try.failure(throwable));
    }

    @Override
    public ReactiveResultPair<INPUT,OUTPUT> onSuccess(BiConsumer<INPUT, ? super OUTPUT> successAction) {
        return (ReactiveResultPair<INPUT, OUTPUT>) super.onSuccess(successAction);
    }

    @Override
    public ReactiveResultPair<INPUT, OUTPUT> onFailure(BiConsumer<INPUT, ? super Throwable> failureAction) {
        return (ReactiveResultPair<INPUT, OUTPUT>) super.onFailure(failureAction);
    }

    public Flux<OUTPUT> toFlux() {
        return Flux.fromStream(super.toStream());
    }

    public Flux<OUTPUT> toFlux(BiConsumer<INPUT, ? super Throwable> failureAction) {
        return Flux.fromStream(super.toStream(failureAction));
    }

}
