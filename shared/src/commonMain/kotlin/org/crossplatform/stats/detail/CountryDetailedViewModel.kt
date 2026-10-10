package org.crossplatform.stats.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.crossplatform.stats.domain.CountryRepository
import org.crossplatform.stats.domain.getNeighbours
import org.crossplatform.stats.ui.model.toCardsUI

class CountryDetailedViewModel(
    private val repository: CountryRepository,)
    : ViewModel()
{
    private val _state = MutableStateFlow(CountryDetailedState())
    val state = _state.asStateFlow()
    fun load(countryId: Int)
    {
        viewModelScope.launch {
            val country = repository.getCountry(countryId)
            val neighbours = repository.getNeighbours(country)
            _state.update {
                it.copy(
                    country = country,
                    neighbours = neighbours.toCardsUI(),
                )
            }
        }
    }
}