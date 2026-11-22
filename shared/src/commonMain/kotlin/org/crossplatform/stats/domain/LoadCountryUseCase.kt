package org.crossplatform.stats.domain

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.crossplatform.stats.domain.model.Country
import org.crossplatform.stats.domain.repository.CountryRepository

class LoadCountryUseCase(
    private val countryRepository: CountryRepository,
    private val pageSize: Int = 15,
) {
    private var page = 1

    fun loadNext(): Flow<Result<Country>> = flow {
        var failed = false
        countryRepository.getCountries(page = page, pageSize = pageSize).collect { result ->
            if (result.isFailure) failed = true
            emit(result)
        }
        if (!failed) {
            page++
        }
    }
}
