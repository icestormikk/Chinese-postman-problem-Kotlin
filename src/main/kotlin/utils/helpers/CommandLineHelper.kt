package utils.helpers

object CommandLineHelper {
    private val logger = LoggingHelper.getLogger("COMMAND_LINE_LOGGER")

    fun <T> fetchArgument(
        argumentsMap: Map<String, String>,
        argument: String,
        isRequired: Boolean = false,
        onTransform: (String) -> T
    ): T? {
        val value = argumentsMap[argument]

        if (!isRequired && value == null) return null

        require(!value.isNullOrBlank()) { "The $argument parameter could not be found. " +
            "Check the correctness of the transmitted parameters." }

        return try {
            onTransform(value)
        } catch (ex: Exception) {
            logger.error { ex.message }
            throw IllegalArgumentException("Error during extraction of parameters from the command line (${ex.message})")
        }
    }
}