package org.crossplatform.stats.ui.detail

import org.crossplatform.stats.domain.model.Country
import org.crossplatform.stats.ui.model.CountryCardUI

data class CountryDetailedState(
    val country: Country? = null,
    val neighbours: List<CountryCardUI> = emptyList(),
)