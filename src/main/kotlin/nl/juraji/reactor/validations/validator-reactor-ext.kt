package nl.juraji.reactor.validations

import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

/**
 * Run synchronous validation on the entry value
 *
 * @param block A [Validator] block.
 * @return The entry [Mono] or a [Mono] of [ValidationException] when validation fails.
 */
fun <T : Any> Mono<T>.validate(block: Validator.(T) -> Unit): Mono<T> =
        this.map { next -> ValidatorImpl().apply { block.invoke(this, next) }.let { next } }

/**
 * Run asynchronous validation on the entry value
 *
 * @param block An [AsyncValidator] block.
 * @return The entry [Mono] or a [Mono] of [ValidationException] when validation fails.
 */
fun <T : Any> Mono<T>.validateAsync(block: AsyncValidator.(T) -> Unit): Mono<T> =
        this.flatMap { next -> AsyncValidatorImpl().apply { block.invoke(this, next) }.run().map { next } }

/**
 * Run synchronous validation on the stream
 *
 * @param block A [Validator] block.
 * @return The entry [Flux] or a [Flux] of [ValidationException] when validation fails.
 */
fun <T : Any> Flux<T>.validate(block: Validator.(T) -> Unit): Flux<T> =
        this.map { next -> ValidatorImpl().apply { block.invoke(this, next) }.let { next } }

/**
 * Run asynchronous validation on the stream
 *
 * @param block An [AsyncValidator] block.
 * @return The entry [Flux] or a [Flux] of [ValidationException] when validation fails.
 */
fun <T : Any> Flux<T>.validateAsync(block: AsyncValidator.(T) -> Unit): Flux<T> =
        this.flatMap { next -> AsyncValidatorImpl().apply { block.invoke(this, next) }.run().map { next } }

