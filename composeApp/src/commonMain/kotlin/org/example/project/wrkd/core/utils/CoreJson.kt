package org.example.project.wrkd.core.utils

import kotlinx.serialization.json.Json

val CoreJson = Json {
    this.isLenient = true
    this.explicitNulls = false
    this.ignoreUnknownKeys = true
    this.encodeDefaults = true
}

inline fun <reified T>Json.decodeSafely(string: String): T? {
    return try {
        this.decodeFromString<T>(string)
    } catch (e: Exception) {
        null
    }
}