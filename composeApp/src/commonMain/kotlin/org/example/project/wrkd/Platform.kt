package org.example.project.wrkd

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform