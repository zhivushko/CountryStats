package org.crossplatform.stats.domain

import org.crossplatform.stats.data.Country
import org.crossplatform.stats.ui.model.CountryCardUI

data class CountryDetailedState(
    val country: Country? = null,
    val neighbours: List<CountryCardUI> = emptyList(),
)