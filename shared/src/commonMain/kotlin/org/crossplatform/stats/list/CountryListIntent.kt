package org.crossplatform.stats.list

sealed interface CountryListIntent {
    data class CardClicked(val id: Int) : CountryListIntent
    data class QueryChanged(val value: String) : CountryListIntent
}