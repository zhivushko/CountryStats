package org.crossplatform.stats.ui.list

import org.crossplatform.stats.ui.model.CountryCardUI
import org.jetbrains.compose.resources.StringResource

sealed interface CountryListState {
    data object Loading : CountryListState

    data class Error(val messageRes: StringResource) : CountryListState

    data class Success(
        val query: String,
        val items: List<CountryCardUI>,
        val isSearching: Boolean = false,
        val isLoadingMore: Boolean = false,
        val loadMoreFailed: Boolean = false,
        val hasMore: Boolean = true,
    ) : CountryListState
}
