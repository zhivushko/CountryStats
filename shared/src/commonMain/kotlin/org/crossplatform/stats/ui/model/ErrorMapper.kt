package org.crossplatform.stats.ui.model

import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.plugins.ResponseException
import kotlinx.io.IOException
import org.crossplatform.stats.domain.repository.CountryNotFound
import org.crossplatform.stats.resources.Res
import org.crossplatform.stats.resources.error_network
import org.crossplatform.stats.resources.error_not_found
import org.crossplatform.stats.resources.error_server
import org.crossplatform.stats.resources.error_timeout
import org.jetbrains.compose.resources.StringResource

fun Throwable.toErrorMessageRes(): StringResource = when (this) {
    is CountryNotFound, is NoSuchElementException -> Res.string.error_not_found
    is HttpRequestTimeoutException -> Res.string.error_timeout
    is IOException -> Res.string.error_network
    is ResponseException -> Res.string.error_server
    else -> {
        val msg = message?.lowercase().orEmpty()
        when {
            msg.contains("timeout") -> Res.string.error_timeout
            msg.contains("network") || msg.contains("connect") || msg.contains("fetch") || msg.contains("unresolved") -> Res.string.error_network
            else -> Res.string.error_server
        }
    }
}
