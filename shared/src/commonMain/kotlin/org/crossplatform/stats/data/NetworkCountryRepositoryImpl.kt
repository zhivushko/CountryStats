package org.crossplatform.stats.data

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.crossplatform.stats.data.network.WorldBankApi
import org.crossplatform.stats.data.network.WorldBankIndicators
import org.crossplatform.stats.data.network.toModel
import org.crossplatform.stats.domain.model.Country
import org.crossplatform.stats.domain.repository.CountryRepository

class NetworkCountryRepositoryImpl(
    private val api: WorldBankApi
) : CountryRepository {

    private val mutex = Mutex()

    private var countries: List<Country>? = null
    private var gdpLoaded = false
    private var detailLoaded = false

    override suspend fun getCountries(): List<Country> {
        ensureBase()
        ensureGdp()
        return countries ?: emptyList()
    }

    override suspend fun getCountry(id: String): Country {
        ensureBase()
        ensureGdp()
        ensureDetail()
        return countries?.firstOrNull { it.id.equals(id, ignoreCase = true) }
            ?: throw NoSuchElementException("Country $id not found")
    }

    private suspend fun ensureBase() {
        mutex.withLock {
            if (countries != null) return
            val dtos = api.safeCall { api.getCountries() }
            val population = api.safeCall {
                api.getIndicatorValues(
                    WorldBankIndicators.population,
                    date = WorldBankIndicators.year
                )
            }
            countries = dtos
                .filter { !it.isAggregate && it.name.isNotBlank() && it.id.isNotBlank() }
                .map { dto ->
                    dto.toModel(popularity = population[dto.id.uppercase()]?.toInt())
                }
        }
    }

    private suspend fun ensureGdp() {
        mutex.withLock {
            if (gdpLoaded || countries == null) return
            val values = api.safeCall {
                api.getIndicatorValues(
                    WorldBankIndicators.gdp,
                    date = WorldBankIndicators.year
                )
            }
            countries = countries?.map { country ->
                country.copy(GDP = values[country.id.uppercase()]?.toLong() ?: country.GDP)
            }
            gdpLoaded = true
        }
    }

    private suspend fun ensureDetail() {
        mutex.withLock {
            if (detailLoaded || countries == null) return
            val urban = api.safeCall {
                api.getIndicatorValues(
                    WorldBankIndicators.urbanShare,
                    date = WorldBankIndicators.year
                )
            }
            val rural = api.safeCall {
                api.getIndicatorValues(
                    WorldBankIndicators.ruralShare,
                    date = WorldBankIndicators.year
                )
            }
            countries = countries?.map { country ->
                country.copy(
                    urb_tot = urban[country.id.uppercase()]?.toFloat() ?: country.urb_tot,
                    rur_tot = rural[country.id.uppercase()]?.toFloat() ?: country.rur_tot
                )
            }
            detailLoaded = true
        }
    }
}