package org.crossplatform.stats.ui.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.crossplatform.stats.domain.GetCountryInteractor
import org.crossplatform.stats.domain.model.LoadStatus
import org.crossplatform.stats.domain.repository.filterByName
import org.crossplatform.stats.ui.model.toCardsUI
import org.crossplatform.stats.ui.model.toErrorMessageRes
import org.crossplatform.stats.ui.navigation.AppViewModel
import org.crossplatform.stats.ui.navigation.Screen

private const val SEARCH_DEBOUNCE_MS = 400L

@OptIn(FlowPreview::class)
class CountryListViewModel(
    private val navigator: AppViewModel,
    private val getCountryInteractor: GetCountryInteractor,
) : ViewModel() {

    private val query = MutableStateFlow("")

    private val appliedQuery: StateFlow<String> = query
        .drop(1)
        .debounce(SEARCH_DEBOUNCE_MS)
        .distinctUntilChanged()
        .stateIn(viewModelScope, SharingStarted.Eagerly, "")

    val state: StateFlow<CountryListState> = combine(
        getCountryInteractor.countryList,
        query,
        appliedQuery,
        getCountryInteractor.status,
    ) { countries, query, appliedQuery, status ->
        if (countries.isEmpty() && query.isBlank()) {
            when (status) {
                LoadStatus.Loading -> CountryListState.Loading
                is LoadStatus.Failed -> CountryListState.Error(status.cause.toErrorMessageRes())
                LoadStatus.Loaded -> CountryListState.Success(query = query, items = emptyList())
            }
        } else {
            val isSearching = query.isNotBlank() && (status is LoadStatus.Loading || query != appliedQuery)
            val filtered = countries.filterByName(query)
            CountryListState.Success(
                query = query,
                items = filtered.toCardsUI(),
                isSearching = isSearching,
                isLoadingMore = query.isBlank() && status is LoadStatus.Loading,
                loadMoreFailed = status is LoadStatus.Failed,
            )
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, CountryListState.Loading)

    init {
        loadNextPage()
        viewModelScope.launch {
            appliedQuery.collect { q ->
                if (q.isNotBlank()) {
                    getCountryInteractor.search(q)
                }
            }
        }
    }

    private fun loadNextPage() {
        viewModelScope.launch {
            getCountryInteractor.loadMore()
        }
    }

    fun onIntent(intent: CountryListIntent) {
        when (intent) {
            is CountryListIntent.CardClicked ->
                navigator.addToBackStack(Screen.Detail(intent.id))

            is CountryListIntent.QueryChanged ->
                query.value = intent.value

            CountryListIntent.LoadMoreRequested -> {
                if (appliedQuery.value.isBlank()) {
                    loadNextPage()
                }
            }

            CountryListIntent.Retry -> {
                if (appliedQuery.value.isNotBlank()) {
                    viewModelScope.launch { getCountryInteractor.search(appliedQuery.value) }
                } else {
                    loadNextPage()
                }
            }
        }
    }
}
