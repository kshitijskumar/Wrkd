package org.example.project.wrkd.utils

import kotlinx.serialization.json.Json

val AppJson: Json = Json {
    this.explicitNulls = false
    this.isLenient = true
    this.encodeDefaults = true
    this.ignoreUnknownKeys = true
    this.prettyPrint = true
}