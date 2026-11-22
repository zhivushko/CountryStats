package org.crossplatform.stats.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.crossplatform.stats.domain.GetCountryInteractor
import org.crossplatform.stats.ui.model.toCardsUI
import org.crossplatform.stats.ui.model.toErrorMessageRes

class CountryDetailedViewModel(
    private val interactor: GetCountryInteractor,
) : ViewModel() {

    private val _state = MutableStateFlow<CountryDetailedState>(CountryDetailedState.Loading)
    val state = _state.asStateFlow()

    private var lastId: String? = null

    fun load(countryId: String) {
        lastId = countryId
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

    fun retry() {
        lastId?.let { load(it) }
    }
}
