package org.crossplatform.stats.domain.repository

import org.crossplatform.stats.domain.model.Country


interface CountryRepository {
    suspend fun getCountries(): List<Country>
    suspend fun getCountry(id: String): Country
}

suspend fun CountryRepository.getCountries(filter: String?): List<Country> {
    return getCountries().filterByName(filter)
}

fun List<Country>.filterByName(filter: String?): List<Country> {
    val needle = filter?.trim().orEmpty()
    if (needle.isEmpty()) return this
    return filter { it.name.contains(needle, ignoreCase = true) }
}

suspend fun CountryRepository.getNeighbours(country: Country): List<Country> {
    val maxSize: Int = 5
    return getCountries()
        .filter { it.region == country.region && it.id != country.id }
        .take(maxSize)
}