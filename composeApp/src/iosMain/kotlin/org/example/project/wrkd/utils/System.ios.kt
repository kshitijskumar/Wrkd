package org.example.project.wrkd.utils

import platform.Foundation.NSDate
import platform.Foundation.timeIntervalSince1970

actual object System {
    actual val currentTimeInMillis: Long
        get() = NSDate().timeIntervalSince1970.toLong() * 1000

}