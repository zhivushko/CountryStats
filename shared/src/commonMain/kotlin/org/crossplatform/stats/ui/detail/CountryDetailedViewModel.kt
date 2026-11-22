package org.crossplatform.stats.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.crossplatform.stats.domain.GetCountryInteractor
import org.crossplatform.stats.ui.model.toCardsUI
import org.crossplatform.stats.ui.model.toErrorMessageRes
import org.crossplatform.stats.ui.navigation.AppViewModel
import org.crossplatform.stats.ui.navigation.Screen

class CountryDetailedViewModel(
    private val countryId: String,
    private val navigator: AppViewModel,
    private val interactor: GetCountryInteractor,
) : ViewModel() {

    private val _state = MutableStateFlow<CountryDetailedState>(CountryDetailedState.Loading)
    val state = _state.asStateFlow()

    init {
        load()
    }

    private fun load() {
        _state.value = CountryDetailedState.Loading

        viewModelScope.launch {
            _state.value = interactor.getById(countryId).fold(
                onSuccess = { country ->
                    val neighbours = interactor.getNeighbours(country)
                    CountryDetailedState.Success(
                        country = country,
                        neighbours = neighbours.toCardsUI(),
                    )
                },
                onFailure = { e ->
                    CountryDetailedState.Error(e.toErrorMessageRes())
                }
            )
        }
    }

    fun onIntent(intent: CountryDetailIntent) {
        when (intent) {
            is CountryDetailIntent.NeighbourClicked -> navigator.addToBackStack(Screen.Detail(intent.id))
            CountryDetailIntent.RetryRequested -> load()
        }
    }
}
