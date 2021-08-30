# stream-try

Utility project to aid dealing with checked and unchecked exceptions in streams.

The project based on https://www.vavr.io/ _Try_.

Stream processing makes it difficult to properly handle errors, as input data lost in subsequent transformations.
This leaves the programmer with a choice: introduce ugly _try-catch_ blocks inside processing functions, or use something similar to _Try_ and not be able to reference input data, which causes failure.

**stream-try** project addresses this issue by introducing two constructs:

### ResultPair<INPUT, OUTPUT> 
The class encloses input value along with _Try_ object with output.

This allows referencing the input data in error handling, such as sending it to _dead-letter queue_.

### TryFunction<INPUT, ResultPair<INPUT, OUTPUT>>
A wrapper class for any _Function<INPUT, OUTPUT>_ to ease exception handling.

## Examples
Example code can be found in [Examples::main()](src/test/java/net/grayswander/streamutils/streamtry/Examples.java).

In all examples:
```
List<String> STRINGS = Arrays.asList("Hello", "bamba", "unchecked", "hey", "checked", "still here");
```
[TestProcessingFunctions](src/test/java/net/grayswander/streamutils/streamtry/TestProcessingFunctions.java)::processFunc throws exceptions on `checked` and `unchecked` inputs, returning some value otherwise.

### Print successful results, ignoring failures

```
STRINGS
    .stream()
    .map(TryFunction.of(TestProcessingFunctions::processFunc))
    .flatMap(ResultPair::toStream)
    .forEach(System.out::println);
```

### Print successful results, side effect on failures
```
STRINGS
    .stream()
    .map(TryFunction.of(TestProcessingFunctions::processFunc))
    .flatMap(resultPair -> resultPair.toStream(Examples::handleFailure))
    .forEach(System.out::println);
```

### Invoke side effect on success and failure
```
STRINGS
    .stream()
    .map(TryFunction.of(TestProcessingFunctions::processFunc))
    .peek(resultPair -> resultPair.onFailure(Examples::handleFailure))
    .peek(resultPair -> resultPair.onSuccess(Examples::handleSuccess))
    .collect(Collectors.toList());
```

### Explicit filter and result extraction
```
STRINGS
    .stream()
    .map(TryFunction.of(TestProcessingFunctions::processFunc))
    .filter(ResultPair::isSuccess)
    .map(ResultPair::getResult)
    .map(Try::get)
    .forEach(System.out::println);
```