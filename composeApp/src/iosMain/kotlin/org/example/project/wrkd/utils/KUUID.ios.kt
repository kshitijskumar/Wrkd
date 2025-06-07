package org.example.project.wrkd.utils

import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

actual object KUUID {
    @OptIn(ExperimentalUuidApi::class)
    actual fun generateId(): String {
        return Uuid.random().toString()
    }
}