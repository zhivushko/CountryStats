package org.crossplatform.stats.domain.model

sealed interface LoadStatus {
    data object Loading : LoadStatus
    data object Loaded : LoadStatus
    data class Failed(val cause: Throwable) : LoadStatus
}