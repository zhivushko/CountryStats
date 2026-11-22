package org.crossplatform.stats.ui.detail

sealed interface CountryDetailIntent {
    data class NeighbourClicked(val id: String) : CountryDetailIntent
    data object RetryRequested : CountryDetailIntent
}
