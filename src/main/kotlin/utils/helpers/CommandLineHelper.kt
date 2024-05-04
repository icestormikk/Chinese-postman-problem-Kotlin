package utils.helpers

class CommandLineHelper {
    private val logger = LoggingHelper().getLogger(CommandLineHelper::class.simpleName.toString())

    fun <T> fetchArgument(
        argumentsMap: Map<String, String>,
        argument: String,
        isRequired: Boolean = false,
        onTransform: (String) -> T
    ): T? {
        val value = argumentsMap[argument]

        if (!isRequired && value == null) return null

        require(!value.isNullOrBlank()) { "The required $argument parameter could not be found. " +
            "Check the correctness of the transmitted parameters." }

        return try {
            val res = onTransform(value)
            logger.info { "The following parameter was successfully received from the command line: $argument" }
            res
        } catch (ex: Exception) {
            throw IllegalArgumentException("Error during extraction of parameters from the command line (${ex.message})")
        }
    }
}