package nl.juraji.reactor.validations

import org.junit.jupiter.api.Test
import reactor.core.publisher.Mono
import reactor.test.StepVerifier

class ValidatorAsyncDslTest {
    @Test
    fun `isTrue should proceed when assertion is true`() {
        val validated = validateAsync {
            isTrue(Mono.just(true)) { "Should not throw" }
        }

        StepVerifier.create(validated)
                .expectNext(true)
                .expectComplete()
                .verify()
    }

    @Test
    fun `isTrue should fail when assertion is false`() {
        val validated = validateAsync {
            isTrue(Mono.just(false)) { "Should throw" }
        }

        StepVerifier.create(validated)
                .expectError(ValidationException::class.java)
                .verify()
    }

    @Test
    fun `isFalse should proceed when assertion is false`() {
        val validated = validateAsync {
            isFalse(Mono.just(false)) { "Should not throw" }
        }

        StepVerifier.create(validated)
                .expectNext(true)
                .expectComplete()
                .verify()
    }

    @Test
    fun `isFalse should fail when assertion is true`() {
        val validated = validateAsync {
            isFalse(Mono.just(true)) { "Should not throw" }
        }

        StepVerifier.create(validated)
                .expectError(ValidationException::class.java)
                .verify()
    }

    @Test
    fun `unless should skip validation when predicate is true`() {
        val validated = validateAsync {
            unless(true) {
                isFalse(Mono.just(true)) { "Should throw" }
            }
        }

        StepVerifier.create(validated)
                .expectNext(true)
                .expectComplete()
                .verify()
    }

    @Test
    fun `unless should run validation when predicate is false`() {
        val validated = validateAsync {
            unless(false) {
                isFalse(Mono.just(true)) { "Should throw" }
            }
        }

        StepVerifier.create(validated)
                .expectError(ValidationException::class.java)
                .verify()
    }

    @Test
    fun `unless should skip validation when predicate mono results in true`() {
        val validated = validateAsync {
            this.unless(Mono.just(true)) {
                isFalse(Mono.just(true)) { "Should throw" }
            }
        }

        StepVerifier.create(validated)
                .expectNext(true)
                .expectComplete()
                .verify()
    }

    @Test
    fun `unless should run validation when predicate mono results in false`() {
        val validated = validateAsync {
            this.unless(Mono.just(false)) {
                isFalse(Mono.just(true)) { "Should throw" }
            }
        }

        StepVerifier.create(validated)
                .expectError(ValidationException::class.java)
                .verify()
    }

    @Test
    fun `synchronous should proceed if assertions block succeed`() {
        val validated = validateAsync {
            synchronous {
                isTrue(true) { "Should not throw" }
                isFalse(false) { "Should not throw" }
            }
        }

        StepVerifier.create(validated)
                .expectNext(true)
                .expectComplete()
                .verify()
    }

    @Test
    fun `synchronous should fail if on or more assertions in block fail`() {
        val validated = validateAsync {
            synchronous {
                isFalse(false) { "Should not throw" }
                isTrue(false) { "Should throw" }
            }
        }

        StepVerifier.create(validated)
                .expectError(ValidationException::class.java)
                .verify()
    }
}