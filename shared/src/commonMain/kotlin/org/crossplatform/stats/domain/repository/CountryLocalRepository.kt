package org.crossplatform.stats.domain.repository

import kotlinx.coroutines.flow.Flow
import org.crossplatform.stats.domain.model.Country

interface CountryLocalRepository : CountryRepository {

    fun observeAll(): Flow<List<Country>>

    suspend fun getAll(): List<Country>

    suspend fun upsert(country: Country)

    suspend fun clear()
}
