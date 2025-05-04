package org.example.project.wrkd.utils

import java.util.UUID

actual object KUUID {
    actual fun generateId(): String {
        return UUID.randomUUID().toString()
    }
}