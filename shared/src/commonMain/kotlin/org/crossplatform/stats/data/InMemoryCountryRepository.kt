package org.crossplatform.stats.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import org.crossplatform.stats.domain.model.Country
import org.crossplatform.stats.domain.repository.CountryLocalRepository
import org.crossplatform.stats.domain.repository.CountryNotFound
import org.crossplatform.stats.domain.repository.filterByName

class InMemoryCountryRepository : CountryLocalRepository {

    private val countries = MutableStateFlow(emptyList<Country>())

    override fun observeAll(): Flow<List<Country>> = countries.asStateFlow()

    override suspend fun getAll(): List<Country> = countries.value

    override fun getCountries(page: Int, pageSize: Int): Flow<Result<Country>> {
        val offset = ((page - 1) * pageSize).coerceAtLeast(0)
        return countries.value
            .drop(offset)
            .take(pageSize)
            .map { Result.success(it) }
            .asFlow()
    }

    override suspend fun getCountry(id: String): Result<Country> =
        countries.value.firstOrNull { it.id.equals(id, ignoreCase = true) }
            ?.let { Result.success(it) }
            ?: Result.failure(CountryNotFound(id))

    override suspend fun searchCountries(query: String): Result<List<Country>> =
        Result.success(countries.value.filterByName(query))

    override suspend fun upsert(country: Country) {
        countries.update { list ->
            val index = list.indexOfFirst { it.id.equals(country.id, ignoreCase = true) }
            if (index >= 0) {
                list.toMutableList().also { it[index] = country }
            } else {
                list + country
            }
        }
    }

    override suspend fun clear() {
        countries.value = emptyList()
    }
}
