package utils.helpers

import mu.KotlinLogging
import org.slf4j.Logger

class LoggingHelper {
    fun getLogger(name: String = Logger.ROOT_LOGGER_NAME) = KotlinLogging.logger(name)
}