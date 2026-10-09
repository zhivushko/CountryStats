package org.crossplatform.stats

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform