package org.example.project.wrkd.utils

import java.lang.System

actual object System {
    actual val currentTimeInMillis: Long get() = System.currentTimeMillis()
}