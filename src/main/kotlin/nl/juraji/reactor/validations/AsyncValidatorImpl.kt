package nl.juraji.reactor.validations

import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

internal class AsyncValidatorImpl : AsyncValidator {
    private val creators: MutableList<() -> Mono<Boolean>> = mutableListOf()
    private var exceptionCreator: (String) -> Throwable = { message -> ValidationException(message) }

    override fun useException(creator: (String) -> Throwable) {
        exceptionCreator = creator
    }

    override fun isTrue(assertion: Mono<Boolean>, message: () -> String) = collect {
        assertion.flatMap { b ->
            if (b) success()
            else fail(message)
        }
    }

    override fun isFalse(assertion: Mono<Boolean>, message: () -> String) = collect {
        assertion.flatMap { b ->
            if (!b) success()
            else fail(message)
        }
    }

    override fun unless(predicate: Boolean, validation: AsyncValidator.() -> Unit) = collect {
        if (predicate) success()
        else AsyncValidatorImpl().apply(validation).run()
    }

    override fun unless(predicate: Mono<Boolean>, validation: AsyncValidator.() -> Unit) = collect {
        predicate.flatMap {
            if (it) success()
            else AsyncValidatorImpl().apply(validation).run()
        }
    }

    override fun synchronous(validation: Validator.() -> Unit) = collect {
        Mono.just(validation)
                .map(::validate)
                .flatMap { success() }
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
