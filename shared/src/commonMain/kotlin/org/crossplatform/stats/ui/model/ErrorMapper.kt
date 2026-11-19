package org.crossplatform.stats.ui.model

import org.crossplatform.stats.resources.Res
import org.crossplatform.stats.resources.error_network
import org.crossplatform.stats.resources.error_not_found
import org.crossplatform.stats.resources.error_server
import org.crossplatform.stats.resources.error_timeout
import org.jetbrains.compose.resources.StringResource

fun Throwable.toErrorMessageRes(): StringResource = when {
    this is NoSuchElementException -> Res.string.error_not_found
    message?.contains("timeout") == true -> Res.string.error_timeout
    message?.contains("network") == true -> Res.string.error_network
    else -> Res.string.error_server
}