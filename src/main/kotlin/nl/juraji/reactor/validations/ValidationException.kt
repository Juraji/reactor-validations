package nl.juraji.reactor.validations

data class ValidationException(
        override val message: String,
) : IllegalArgumentException(message)
