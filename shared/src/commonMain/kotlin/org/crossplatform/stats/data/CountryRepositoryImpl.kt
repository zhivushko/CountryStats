package org.crossplatform.stats.data

import org.crossplatform.stats.domain.CountryRepository

class CountryRepositoryImpl : CountryRepository
{
    override suspend fun getCountries(): List<Country> {
        return CountryMocks
    }

    override fun getCountry(id: Int): Country
    {
        return CountryMocks.first { it.id == id}
    }
}