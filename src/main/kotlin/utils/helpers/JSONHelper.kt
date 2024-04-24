package utils.helpers

import kotlinx.serialization.json.Json

object JSONHelper {
    private val _jsonFormatter = Json {
        encodeDefaults = true
        prettyPrint = true
    }

    fun getInstance() = _jsonFormatter
}