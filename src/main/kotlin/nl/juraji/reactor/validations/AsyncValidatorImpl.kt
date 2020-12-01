package nl.juraji.reactor.validations

import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

internal class AsyncValidatorImpl(
    private var exceptionCreator: (String) -> Throwable = { message -> ValidationException(message) }
) : AsyncValidator {
    private val creators: MutableList<() -> Mono<Boolean>> = mutableListOf()

    override fun useException(creator: (String) -> Throwable): AsyncValidator {
        exceptionCreator = creator
        return this
    }

    override fun isTrue(assertion: Mono<Boolean>, message: () -> String): AsyncValidator {
        collect {
            assertion.flatMap { b ->
                if (b) success()
                else fail(message)
            }
        }

        return this
    }

    override fun isFalse(assertion: Mono<Boolean>, message: () -> String): AsyncValidator {
        collect {
            assertion.flatMap { b ->
                if (!b) success()
                else fail(message)
            }
        }

        return this
    }

    override fun unless(predicate: Boolean, validation: AsyncValidator.() -> Unit): AsyncValidator {
        collect {
            if (predicate) success()
            else AsyncValidatorImpl(exceptionCreator)
                .apply(validation)
                .run()
        }

        return this
    }

    override fun unless(predicate: Mono<Boolean>, validation: AsyncValidator.() -> Unit): AsyncValidator {
        collect {
            predicate.flatMap {
                if (it) success()
                else AsyncValidatorImpl(exceptionCreator)
                    .apply(validation)
                    .run()
            }
        }

        return this
    }

    override fun synchronous(validation: Validator.() -> Unit): AsyncValidator {
        collect {
            Mono.just(validation)
                .map { ValidatorImpl(exceptionCreator).apply(it) }
                .flatMap { success() }
        }

        return this
    }

    fun run(): Mono<Boolean> = Flux
        .fromIterable(creators)
        .flatMap { it.invoke() }
        .all { it }

    private fun collect(creator: () -> Mono<Boolean>) {
        creators.add(creator)
    }

    private fun success(): Mono<Boolean> = Mono.just(true)
    private fun fail(message: () -> String): Mono<Boolean> = Mono.error { exceptionCreator(message()) }
}
