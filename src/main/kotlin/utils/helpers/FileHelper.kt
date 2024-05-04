package utils.helpers

import java.io.File

class FileHelper {
    private val logger = LoggingHelper().getLogger(FileHelper::class.simpleName.toString())

    fun writeTo(filepath: String, content: String) {
        try {
            val file = File(filepath)

            val buffer = file.bufferedWriter()
            with(buffer) {
                write(content)
                close()
            }
            logger.info { "Writing to the $filepath has been completed successfully" }
        } catch (ex: Error) {
            val message = "Error while writing to $filepath: ${ex.message}"
            logger.error { message }
            throw Error(message)
        }
    }

    fun <T> readFrom(filepath: String, onTransform: (content: String) -> T): T {
        try {
            val file = File(filepath)
            val stream = file.inputStream()
            val content = stream.readBytes().toString(Charsets.UTF_8)
            val result = onTransform(content)

            logger.info { "Data from the $filepath has been successfully read" }
            return result
        } catch (ex: Error) {
            val message = "Error while writing to $filepath: ${ex.message}"
            logger.error { message }
            throw Error(message)
        }
    }
}