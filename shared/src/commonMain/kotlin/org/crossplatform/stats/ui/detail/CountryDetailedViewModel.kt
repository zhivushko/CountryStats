package org.crossplatform.stats.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.crossplatform.stats.domain.repository.CountryRepository
import org.crossplatform.stats.domain.repository.getNeighbours
import org.crossplatform.stats.ui.model.toCardsUI
import org.crossplatform.stats.ui.model.toErrorMessageRes

class CountryDetailedViewModel(
    private val repository: CountryRepository,
) : ViewModel() {

    private val _state = MutableStateFlow<CountryDetailedState>(CountryDetailedState.Loading)
    val state = _state.asStateFlow()

    private var lastId: String? = null

    fun load(countryId: String) {
        lastId = countryId
        _state.value = CountryDetailedState.Loading

        viewModelScope.launch {
            _state.value = try {
                val country = repository.getCountry(countryId)
                val neighbours = repository.getNeighbours(country)
                CountryDetailedState.Success(
                    country = country,
                    neighbours = neighbours.toCardsUI(),
                )
            } catch (e: Throwable) {
                CountryDetailedState.Error(e.toErrorMessageRes())
            }
        }
    }

    fun retry() {
        lastId?.let { load(it) }
    }
}