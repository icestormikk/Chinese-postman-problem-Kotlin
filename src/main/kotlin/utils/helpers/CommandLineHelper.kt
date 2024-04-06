package utils.helpers

object CommandLineHelper {
    fun <T> fetchArgument(
        argumentsMap: Map<String, String>, argument: String, onTransform: (String) -> T
    ): T {
        val value = argumentsMap[argument]
        if (value.isNullOrBlank()) {
            throw IllegalArgumentException("The $argument parameter could not be found. " +
                    "Check the correctness of the transmitted parameters.")
        }

        return try {
            onTransform(value)
        } catch (ex: Error) {
            throw IllegalArgumentException("Error during extraction of parameters from the command line: ${ex.message}")
        }
    }
}