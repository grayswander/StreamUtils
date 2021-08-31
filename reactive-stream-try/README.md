# stream-try

Utility project to aid dealing with checked and unchecked exceptions in streams.

The project based on https://www.vavr.io/ _Try_.

Stream processing makes it difficult to properly handle errors, as input data lost in subsequent transformations.
This leaves the programmer with a choice: introduce ugly _try-catch_ blocks inside processing functions, or use something similar to _Try_ and not be able to reference input data, which causes failure.

**reactive-stream-try** project addresses this issue by introducing two constructs:

### ReactiveResultPair<INPUT, OUTPUT> 
The class encloses input value along with _Try_ object with output.

This allows referencing the input data in error handling, such as sending it to _dead-letter queue_.

### ReactiveTryFunction<INPUT, ReactiveResultPair<INPUT, OUTPUT>>
A wrapper class for any _Function<INPUT, OUTPUT>_ to ease exception handling.

## Examples
Example code can be found in [ExamplesReactor::main()](src/test/java/net/grayswander/streamutils/streamtry/ExamplesReactor.java).

In all examples:
```
List<String> STRINGS = Arrays.asList("Hello", "bamba", "unchecked", "hey", "checked", "still here");
```
[TestProcessingFunctions](../stream-try/src/testFixtures/java/net/grayswander/streamutils/streamtry/TestProcessingFunctions.java)::processFunc throws exceptions on `checked` and `unchecked` inputs, returning some value otherwise.

### Print successful results, ignoring failures

```
Flux.fromIterable(STRINGS)
    .map(ReactiveTryFunction.of(TestProcessingFunctions::processFunc))
    .flatMap(ReactiveResultPair::toFlux)
    .subscribe(System.out::println);
```

### Print successful results, side effect on failures
```
Flux.fromIterable(STRINGS)
    .map(ReactiveTryFunction.of(TestProcessingFunctions::processFunc))
    .flatMap(resultPair -> resultPair.toFlux(ExamplesReactor::handleFailure))
    .subscribe(System.out::println);
```

### Invoke side effect on success and failure
```
Flux.fromIterable(STRINGS)
    .map(ReactiveTryFunction.of(TestProcessingFunctions::processFunc))
    .doOnNext(resultPair -> resultPair.onFailure(ExamplesReactor::handleFailure))
    .doOnNext(resultPair -> resultPair.onSuccess(ExamplesReactor::handleSuccess))
    .subscribe();
```

### Explicit filter and result extraction
```
Flux.fromIterable(STRINGS)
    .map(ReactiveTryFunction.of(TestProcessingFunctions::processFunc))
    .filter(ReactiveResultPair::isSuccess)
    .map(ReactiveResultPair::getResult)
    .map(Try::get)
    .subscribe(System.out::println);
```