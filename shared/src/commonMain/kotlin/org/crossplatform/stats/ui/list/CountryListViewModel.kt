package org.crossplatform.stats.ui.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.crossplatform.stats.domain.model.Country
import org.crossplatform.stats.domain.repository.CountryRepository
import org.crossplatform.stats.domain.repository.filterByName
import org.crossplatform.stats.domain.model.LoadStatus
import org.crossplatform.stats.ui.model.toCardsUI
import org.crossplatform.stats.ui.model.toErrorMessageRes
import org.crossplatform.stats.ui.navigation.AppViewModel
import org.crossplatform.stats.ui.navigation.Screen

private const val SEARCH_DEBOUNCE_MS = 400L
private const val PAGE_SIZE = 10
private const val LOAD_MORE_DELAY_MS = 800L

@OptIn(FlowPreview::class)
class CountryListViewModel(
    private val navigator: AppViewModel,
    private val repository: CountryRepository,
) : ViewModel() {

    private val query = MutableStateFlow("")

    private val appliedQuery: StateFlow<String> = query
        .drop(1)
        .debounce(SEARCH_DEBOUNCE_MS)
        .distinctUntilChanged()
        .stateIn(viewModelScope, SharingStarted.Eagerly, "")

    /** Все страны из репозитория. */
    private val allCountries = MutableStateFlow<List<Country>>(emptyList())

    /** Сколько элементов уже показано. */
    private val visibleCount = MutableStateFlow(0)

    private val status = MutableStateFlow<LoadStatus>(LoadStatus.Loading)

    val state: StateFlow<CountryListState> = combine(
        allCountries,
        query,
        appliedQuery,
        visibleCount,
        status,
    ) { countries, query, appliedQuery, visibleCount, status ->
        val filtered = countries.filterByName(appliedQuery)

        if (filtered.isEmpty()) {
            when (status) {
                LoadStatus.Loading -> CountryListState.Loading
                is LoadStatus.Failed ->
                    CountryListState.Error(status.cause.toErrorMessageRes())
                LoadStatus.Loaded ->
                    CountryListState.Success(query = query, items = emptyList(), hasMore = false)
            }
        } else {
            val shown = filtered.take(visibleCount.coerceAtLeast(PAGE_SIZE))
            CountryListState.Success(
                query = query,
                items = shown.toCardsUI(),
                isLoadingMore = status is LoadStatus.Loading && visibleCount > 0,
                loadMoreFailed = status is LoadStatus.Failed,
                hasMore = shown.size < filtered.size,
            )
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, CountryListState.Loading)

    init {
        reload()
    }

    fun onIntent(intent: CountryListIntent) {
        when (intent) {
            is CountryListIntent.CardClicked ->
                navigator.addToBackStack(Screen.Detail(intent.id))

            is CountryListIntent.QueryChanged -> {
                query.value = intent.value
                visibleCount.value = PAGE_SIZE
            }

            CountryListIntent.LoadMoreRequested -> loadMore()

            CountryListIntent.Retry -> reload()
        }
    }

    private fun reload() {
        viewModelScope.launch {
            status.value = LoadStatus.Loading
            visibleCount.value = 0
            try {
                allCountries.value = repository.getCountries()
                visibleCount.value = PAGE_SIZE
                status.value = LoadStatus.Loaded
            } catch (e: Throwable) {
                status.value = LoadStatus.Failed(e)
            }
        }
    }

    private fun loadMore() {
        val current = state.value
        if (current !is CountryListState.Success || !current.hasMore) return
        if (status.value is LoadStatus.Loading) return

        viewModelScope.launch {
            status.value = LoadStatus.Loading
            delay(LOAD_MORE_DELAY_MS) // искусственная задержка, чтобы колесо было видно
            visibleCount.value += PAGE_SIZE
            status.value = LoadStatus.Loaded
        }
    }
}