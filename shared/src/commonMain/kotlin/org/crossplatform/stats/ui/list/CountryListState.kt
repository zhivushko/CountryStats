package org.crossplatform.stats.ui.list

import org.crossplatform.stats.domain.model.Country

data class CountryListState(
    val query: String = "",
    val items: List<Country> = emptyList()
)
