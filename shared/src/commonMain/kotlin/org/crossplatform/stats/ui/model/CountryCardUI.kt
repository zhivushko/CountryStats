package org.crossplatform.stats.ui.model

import org.crossplatform.stats.data.Country

data class CountryCardUI(
    val id: Int,
    val code: String,
    val name: String,
    val popularity: Int,
    val GDP: Long,
)
fun Country.toCardUI(): CountryCardUI = CountryCardUI(
    code = code,
    name = name,
    popularity = popularity,
    id = id,
    GDP = GDP
)

fun List<Country>.toCardsUI(): List<CountryCardUI> = map { it.toCardUI() }