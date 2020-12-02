package nl.juraji.reactor.validations

import reactor.core.publisher.Mono

interface AsyncValidator {

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
