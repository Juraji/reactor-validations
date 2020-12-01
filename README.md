# reactor-validations

A Kotlin based validations api with Project Reactor extensions

## Why
Think the `Assert` utility from Spring Framework or Axon Framework, just with __not__ using `IllegalArgumentException`
_(Which in my opinion should be used for technical validation!!!)_, but with using a dedicated `ValidationException`.
Or maybe you would like to be able to use your own custom exception...

Well now you can :D

# Examples:
Synchronous validation:
```kotlin
import nl.juraji.reactor.validations.validate

fun main(args: Array<String>) {
    
    validate {
        useException(MyCustomException::class) // Will use internal `ValidationException` when omitted.
        
        isTrue(someCheck()) { "Check should result in true" }
    }
    
}
```

Or asynchronous validation with [Project Reactor](https://projectreactor.io/):
```kotlin
import nl.juraji.reactor.validations.validateAsync

validateAsync {
    // Accepts a Mono<Boolean>
    isTrue(Mono.just(true)) { "Check should result in true" }
    
    synchronous {
        // Maybe do some synchronous checks in here as well
        isFalse(args.isEmpty()) { "No args supplied" }
    }
}
    .flatMap(this::continueWithOtherMono)
    // ...
```

Or use directly in a [Project Reactor](https://projectreactor.io/) stream:
```kotlin
import nl.juraji.reactor.validations.validateAsync

// Synchronous
myReactiveService.stuffToValidate()
    .validate {
        isTrue(Mono.just(true)) { "Check should result in true" }
    }
    .map { ... }
    .filter { ... }
    // ...

// Or asynchronous
myReactiveService.stuffToValidate()
    .validateAsync {
        unless(it == null) { // Only when predicate is true, also supports a Mono<Boolean> as predicate
            isTrue(Mono.just(true)) { "Check should result in true" }   
        }
    }
    .map { ... }
    .filter { ... }
// ...
```
