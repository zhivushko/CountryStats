package org.crossplatform.stats.domain

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.sync.Mutex
import org.crossplatform.stats.domain.model.Country
import org.crossplatform.stats.domain.model.LoadStatus
import org.crossplatform.stats.domain.repository.CountryLocalRepository
import org.crossplatform.stats.domain.repository.CountryNotFound
import org.crossplatform.stats.domain.repository.CountryRepository
import org.crossplatform.stats.domain.repository.filterByName
import org.crossplatform.stats.domain.repository.findNeighbours

class GetCountryInteractor(
    private val loadCountryUseCase: LoadCountryUseCase,
    private val remote: CountryRepository,
    private val local: CountryLocalRepository,
) {
    val countryList: Flow<List<Country>> = local.observeAll()

    private val _status = MutableStateFlow<LoadStatus>(LoadStatus.Loading)
    val status: StateFlow<LoadStatus> = _status.asStateFlow()

    private val mutex = Mutex()

    suspend fun loadMore() {
        if (!mutex.tryLock()) return
        try {
            loadNextPage()
        } finally {
            mutex.unlock()
        }
    }

    suspend fun search(query: String) {
        val clean = query.trim()
        if (clean.isBlank()) return

        _status.value = LoadStatus.Loading
        val result = remote.searchCountries(clean)
        result.fold(
            onSuccess = { countries ->
                countries.forEach { local.upsert(it) }
                _status.value = LoadStatus.Loaded
            },
            onFailure = { error ->
                val hasLocal = local.getAll().filterByName(clean).isNotEmpty()
                _status.value = if (hasLocal) LoadStatus.Loaded else LoadStatus.Failed(error)
            }
        )
    }

    suspend fun getById(id: String): Result<Country> {
        val cached = local.getCountry(id).getOrNull()
        if (cached != null && (cached.urb_tot > 0f || cached.rur_tot > 0f)) {
            return Result.success(cached)
        }
        return remote.getCountry(id)
            .onSuccess { local.upsert(it) }
            .recoverCatching {
                cached ?: throw it
            }
    }

    suspend fun getNeighbours(country: Country): List<Country> {
        return local.getAll().findNeighbours(country)
    }

    private suspend fun loadNextPage() {
        _status.value = LoadStatus.Loading
        var failure: Throwable? = null
        loadCountryUseCase.loadNext().collect { result ->
            result.fold(
                onSuccess = { local.upsert(it) },
                onFailure = { failure = it },
            )
        }
        _status.value = failure?.let { LoadStatus.Failed(it) } ?: LoadStatus.Loaded
    }
}
