package org.crossplatform.stats.data

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.crossplatform.stats.core.suspendRunCatching
import org.crossplatform.stats.data.network.WorldBankApi
import org.crossplatform.stats.data.network.WorldBankIndicators
import org.crossplatform.stats.data.network.dto.CountryDto
import org.crossplatform.stats.data.network.toModel
import org.crossplatform.stats.domain.model.Country
import org.crossplatform.stats.domain.repository.CountryNotFound
import org.crossplatform.stats.domain.repository.CountryRepository

class NetworkCountryRepositoryImpl(
    private val api: WorldBankApi,
) : CountryRepository {

    private val metaMutex = Mutex()
    private var cachedMeta: List<CountryDto>? = null

    override fun getCountries(page: Int, pageSize: Int): Flow<Result<Country>> = flow {
        val pageResult = api.fetchCountriesPage(page = page, perPage = pageSize)
            .getOrElse {
                emit(Result.failure(it))
                return@flow
            }

        val validDtos = pageResult.items.filter {
            !it.isAggregate && it.name.isNotBlank() && it.id.isNotBlank()
        }

        validDtos.forEach { dto ->
            val summaryResult = fetchCountrySummary(dto)
            val country = summaryResult.getOrNull()
            if (country != null && country.popularity > 0 && country.GDP > 0) {
                emit(Result.success(country))
            }
        }
    }

    override suspend fun getCountry(id: String): Result<Country> = suspendRunCatching {
        coroutineScope {
            val dto = api.fetchCountry(id).getOrThrow() ?: throw CountryNotFound(id)

            val popAsync = async {
                api.fetchCountryIndicator(id, WorldBankIndicators.population, WorldBankIndicators.year).getOrNull()
            }
            val gdpAsync = async {
                api.fetchCountryIndicator(id, WorldBankIndicators.gdp, WorldBankIndicators.year).getOrNull()
            }
            val urbAsync = async {
                api.fetchCountryIndicator(id, WorldBankIndicators.urbanShare, WorldBankIndicators.year).getOrNull()
            }
            val rurAsync = async {
                api.fetchCountryIndicator(id, WorldBankIndicators.ruralShare, WorldBankIndicators.year).getOrNull()
            }

            val pop = popAsync.await()?.toInt() ?: 0
            val gdp = gdpAsync.await()?.toLong() ?: 0L
            val urb = urbAsync.await()?.toFloat() ?: 0f
            val rur = rurAsync.await()?.toFloat() ?: 0f

            dto.toModel(popularity = pop, gdp = gdp, urbTot = urb, rurTot = rur)
        }
    }

    override suspend fun searchCountries(query: String): Result<List<Country>> = suspendRunCatching {
        val needle = query.trim()
        if (needle.isBlank()) return@suspendRunCatching emptyList()

        coroutineScope {
            val allMeta = getOrFetchAllCountryMeta()
            val matches = allMeta.filter { dto ->
                !dto.isAggregate && (
                    dto.name.contains(needle, ignoreCase = true) ||
                    dto.id.equals(needle, ignoreCase = true) ||
                    dto.iso2Code.equals(needle, ignoreCase = true)
                )
            }.take(10)

            matches.map { dto ->
                async {
                    fetchCountrySummary(dto).getOrNull()
                }
            }.awaitAll()
                .filterNotNull()
                .filter { it.popularity > 0 && it.GDP > 0 }
        }
    }

    private suspend fun fetchCountrySummary(dto: CountryDto): Result<Country> = suspendRunCatching {
        coroutineScope {
            val popAsync = async {
                api.fetchCountryIndicator(dto.id, WorldBankIndicators.population, WorldBankIndicators.year).getOrNull()
            }
            val gdpAsync = async {
                api.fetchCountryIndicator(dto.id, WorldBankIndicators.gdp, WorldBankIndicators.year).getOrNull()
            }
            val pop = popAsync.await()?.toInt() ?: 0
            val gdp = gdpAsync.await()?.toLong() ?: 0L
            dto.toModel(popularity = pop, gdp = gdp)
        }
    }

    private suspend fun getOrFetchAllCountryMeta(): List<CountryDto> = metaMutex.withLock {
        cachedMeta?.let { return it }
        val result = api.fetchCountriesPage(page = 1, perPage = 300).getOrNull()?.items.orEmpty()
        cachedMeta = result
        result
    }
}
