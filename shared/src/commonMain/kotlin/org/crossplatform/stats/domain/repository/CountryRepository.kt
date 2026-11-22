package org.crossplatform.stats.domain.repository

import kotlinx.coroutines.flow.Flow
import org.crossplatform.stats.domain.model.Country

interface CountryRepository {
    fun getCountries(page: Int, pageSize: Int): Flow<Result<Country>>

    suspend fun getCountry(id: String): Result<Country>

    suspend fun searchCountries(query: String): Result<List<Country>>
}

class CountryNotFound(val id: String) : NoSuchElementException("Country $id not found")

fun List<Country>.filterByName(filter: String?): List<Country> {
    val needle = filter?.trim().orEmpty()
    if (needle.isEmpty()) return this
    return filter {
        it.name.contains(needle, ignoreCase = true) ||
                it.id.equals(needle, ignoreCase = true) ||
                it.code.equals(needle, ignoreCase = true)
    }
}

fun List<Country>.findNeighbours(country: Country, maxSize: Int = 5): List<Country> {
    if (country.region.isBlank()) return emptyList()
    return filter {
        it.region.equals(country.region, ignoreCase = true) &&
                !it.id.equals(country.id, ignoreCase = true)
    }.take(maxSize)
}
