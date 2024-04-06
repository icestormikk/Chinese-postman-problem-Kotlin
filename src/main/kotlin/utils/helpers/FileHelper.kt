package utils.helpers

import java.io.File

object FileHelper {
    fun writeTo(filepath: String, content: String) {
        try {
            val file = File(filepath)

            val buffer = file.bufferedWriter()
            with(buffer) {
                write(content)
                close()
            }
        } catch (ex: Error) {
            throw Error("Error while writing to $filepath: ${ex.message}")
        }
    }

    fun <T> readFrom(filepath: String, onTransform: (content: String) -> T): T {
        try {
            val file = File(filepath)
            val stream = file.inputStream()
            val content = stream.readBytes().toString(Charsets.UTF_8)
            return onTransform(content)
        } catch (ex: Error) {
            throw Error("Error while writing to $filepath: ${ex.message}")
        }
    }
}