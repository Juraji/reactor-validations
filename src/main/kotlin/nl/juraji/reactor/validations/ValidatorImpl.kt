package nl.juraji.reactor.validations

internal class ValidatorImpl(
    private var exceptionCreator: (String) -> Throwable = { message -> ValidationException(message) }
) : Validator {

    override fun useException(creator: (String) -> Throwable): Validator {
        exceptionCreator = creator
        return this
    }

    override fun isTrue(assertion: Boolean, message: () -> String): Validator {
        if (!assertion) fail(message)
        return this
    }

    override fun isFalse(assertion: Boolean, message: () -> String): Validator {
        if (assertion) fail(message)
        return this
    }

    override fun isNotNull(value: Any?, message: () -> String): Validator {
        if (value == null) fail(message)
        return this
    }

    override fun unless(predicate: Boolean, validation: Validator.() -> Unit): Validator {
        if (!predicate) validation.invoke(this)
        return this
    }

    override fun isNotBlank(value: String?, message: () -> String): Validator {
        if (value.isNullOrBlank()) fail(message)
        return this
    }

    override fun isNotEmpty(collection: Collection<Any>, message: () -> String): Validator {
        if (collection.isEmpty()) fail(message)
        return this
    }

    override fun fail(message: () -> String): Nothing = throw exceptionCreator.invoke(message())
}
