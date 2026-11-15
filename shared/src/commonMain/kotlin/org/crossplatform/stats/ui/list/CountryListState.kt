package org.crossplatform.stats.ui.list

import org.crossplatform.stats.domain.Country

data class CountryListState(
    val query: String = "",
    val items: List<Country> = emptyList()
)
