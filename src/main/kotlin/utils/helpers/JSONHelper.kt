package utils.helpers

import kotlinx.serialization.json.Json

class JSONHelper {
    private val _jsonFormatter = Json {
        encodeDefaults = true
        prettyPrint = true
    }

    fun getInstance() = _jsonFormatter
}