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

## Api

### DSL Entry Points
```kotlin
/**
 * Initialize a new asynchronous validation block.
 *
 * @param block An [AsyncValidator] block.
 * @return A [Mono] resulting in `true` when validation succeeds
 * or a [Mono] of [ValidationException] when validation fails.
 */
fun validateAsync(block: AsyncValidator.() -> Unit): Mono<Boolean>

/**
 * Initialize a new validation block.
 *
 * @param block A [Validator] block.
 * @throws ValidationException When a validation fails.
 */
fun validate(block: Validator.() -> Unit): Validator
```

### Synchronous Validator DSL interface
```kotlin
import nl.juraji.reactor.validations.validate

validate {
    /**
     * Override the default [ValidationException] with a custom exception creator
     *
     * @param creator A [Function] which returns the exception to throw based on the input message
     */
    fun useException(creator: (String) -> Throwable): Validator

    /**
     * Validate that [assertion] equals to `true`.
     *
     * @param assertion The [Boolean] value to validate.
     * @throws ValidationException When [assertion] equals to `false`.
     */
    fun isTrue(assertion: Boolean, message: () -> String): Validator

    /**
     * Validate that [assertion] equals to `false`.
     *
     * @param assertion The [Boolean] value to validate.
     * @param message A [Function] which results in a descriptive message.
     * @throws ValidationException When [assertion] equals to `true`.
     */
    fun isFalse(assertion: Boolean, message: () -> String): Validator

    /**
     * Validate that [value] is not `null` or [Unit].
     *
     * @param value The value to validate.
     * @param message A [Function] which results in a descriptive message.
     * @throws ValidationException When [value] equals to `null` or [Unit].
     */
    fun isNotNull(value: Any?, message: () -> String): Validator

    /**
     * Validate that [value] is not `null` or blank.
     *
     * @param value The value to validate.
     * @param message A [Function] which results in a descriptive message.
     * @throws ValidationException When [value] equals to `null` or is blank.
     */
    fun isNotBlank(value: String?, message: () -> String): Validator

    /**
     * Validate that [collection] is not empty.
     *
     * @param collection A [Collection].
     * @param message A [Function] which results in a descriptive message.
     * @throws ValidationException When [collection] is empty.
     */
    fun isNotEmpty(collection: Collection<Any>, message: () -> String): Validator

    /**
     * Ignore [validation] block when [predicate] equals to `true`.
     *
     * @param predicate A [Boolean] value whether to run [validation] block or not.
     * @param validation A new [Validator] block.
     */
    fun unless(predicate: Boolean, validation: Validator.() -> Unit): Validator

    /**
     * Fail with [message].
     *
     * @param message A [Function] which results in a descriptive message.
     * @throws ValidationException Always with [message] as exception message
     */
    fun fail(message: () -> String): Nothing
}

```

### Asynchronous Validator DSL interface
```kotlin
import nl.juraji.reactor.validations.validate

validateAsync {
    /**
     * Override the default [ValidationException] with a custom exception creator
     *
     * @param creator A [Function] which returns the exception to continue with based on the input message
     */
    fun useException(creator: (String) -> Throwable): AsyncValidator

    /**
     * Assert that [assertion] results in `true`.
     *
     * @param assertion A [Mono] resulting in a [Boolean].
     * @param message A [Function] which results in the validation exception message.
     */
    fun isTrue(assertion: Mono<Boolean>, message: () -> String): AsyncValidator

    /**
     * Assert that [assertion] results in `false`.
     *
     * @param assertion A [Mono] resulting in a [Boolean].
     * @param message A [Function] which results in the validation exception message.
     */
    fun isFalse(assertion: Mono<Boolean>, message: () -> String): AsyncValidator

    /**
     * Assert given mono completes
     *
     * @param mono The [Mono] to run
     * @param message Optional: A [Function] which results in the validation exception message.
     * When omitted the resulting [ValidationException] will inherit the emitted error message
     */
    fun <T : Any> succeeds(mono: Mono<T>, message: (() -> String)? = null): AsyncValidator

    /**
     * Run [validation] block only when [predicate] is `false`.
     *
     * @param predicate A [Boolean] value whether to run [validation] or not.
     * @param validation A new [AsyncValidator] block.
     */
    fun unless(predicate: Boolean, validation: AsyncValidator.() -> Unit): AsyncValidator

    /**
     * Run [validation] block only when [predicate] results in `false`.
     *
     * @param predicate A [Mono] of type [Boolean] whether to run [validation] or not.
     * @param validation A new [AsyncValidator] block.
     */
    fun unless(predicate: Mono<Boolean>, validation: AsyncValidator.() -> Unit): AsyncValidator

    /**
     * Run a synchronous [Validator] block as part of this validation.
     *
     * @param validation A new [Validator] block
     */
    fun synchronous(validation: Validator.() -> Unit): AsyncValidator
}
```

### Reactor extension functions
```kotlin
/**
 * Run synchronous validation on the entry value
 *
 * @param block A [Validator] block.
 * @return The entry [Mono] or a [Mono] of [ValidationException] when validation fails.
 */
fun <T : Any> Mono<T>.validate(block: Validator.(T) -> Unit): Mono<T>

/**
 * Run asynchronous validation on the entry value
 *
 * @param block An [AsyncValidator] block.
 * @return The entry [Mono] or a [Mono] of [ValidationException] when validation fails.
 */
fun <T : Any> Mono<T>.validateAsync(block: AsyncValidator.(T) -> Unit): Mono<T>

/**
 * Run synchronous validation on the stream
 *
 * @param block A [Validator] block.
 * @return The entry [Flux] or a [Flux] of [ValidationException] when validation fails.
 */
fun <T : Any> Flux<T>.validate(block: Validator.(T) -> Unit): Flux<T>

/**
 * Run asynchronous validation on the stream
 *
 * @param block An [AsyncValidator] block.
 * @return The entry [Flux] or a [Flux] of [ValidationException] when validation fails.
 */
fun <T : Any> Flux<T>.validateAsync(block: AsyncValidator.(T) -> Unit): Flux<T>
```
