package org.crossplatform.stats.ui.list

sealed interface CountryListIntent {
    data class CardClicked(val id: String) : CountryListIntent
    data class QueryChanged(val value: String) : CountryListIntent
}