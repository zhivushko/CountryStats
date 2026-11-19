package org.crossplatform.stats.ui.detail

import org.crossplatform.stats.domain.model.Country
import org.crossplatform.stats.ui.model.CountryCardUI
import org.jetbrains.compose.resources.StringResource

sealed interface CountryDetailedState {

    data object Loading : CountryDetailedState

    data class Error(val messageRes: StringResource) : CountryDetailedState

    data class Success(
        val country: Country,
        val neighbours: List<CountryCardUI>,
    ) : CountryDetailedState
}