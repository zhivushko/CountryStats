package org.crossplatform.stats.domain

import org.crossplatform.stats.data.Country

data class CountryListState(
    val query: String = "",
    val items: List<Country> = emptyList()
)
