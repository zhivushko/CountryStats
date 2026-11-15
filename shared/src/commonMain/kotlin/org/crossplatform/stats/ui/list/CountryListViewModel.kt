package org.crossplatform.stats.ui.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.crossplatform.stats.domain.repository.CountryRepository
import org.crossplatform.stats.domain.repository.getCountries
import org.crossplatform.stats.ui.navigation.AppViewModel
import org.crossplatform.stats.ui.navigation.Screen

class CountryListViewModel(
    private val navigator: AppViewModel,
    private val repository: CountryRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(CountryListState())
    val state: StateFlow<CountryListState> = _state.asStateFlow()

    init {
        search(filter = null)
    }

    fun onIntent(intent: CountryListIntent) {
        when (intent) {
            is CountryListIntent.CardClicked -> {
                navigator.addToBackStack(Screen.Detail(intent.id))
            }
            is CountryListIntent.QueryChanged -> {
                _state.update { it.copy(query = intent.value) }
                search(filter = intent.value)
            }
        }
    }

    private fun search(filter: String?) {
        viewModelScope.launch {
            val items = repository.getCountries(filter)
            _state.update { it.copy(items = items) }
        }
    }
}