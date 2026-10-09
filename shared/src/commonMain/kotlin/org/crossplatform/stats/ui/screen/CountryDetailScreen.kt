package org.crossplatform.stats.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import org.crossplatform.stats.data.Country
import org.crossplatform.stats.domain.getNeighbours
import org.crossplatform.stats.data.CountryRepositoryImpl
import org.crossplatform.stats.domain.CountryDetailedState
import org.crossplatform.stats.ui.components.CountryDetailed
import org.crossplatform.stats.ui.model.CountryCardUI
import org.crossplatform.stats.ui.model.toCardsUI

@Composable
fun CountryDetailScreen(
    state: CountryDetailedState,
    onCountryClick: (Int) -> Unit,
    modifier: Modifier = Modifier.fillMaxSize(),
) {
    val country = state.country
    val neighbours = state.neighbours

    Column(modifier =modifier.verticalScroll(rememberScrollState())) {
        CountryDetailed(
            country = country as Country,
            neighbours = neighbours,
            modifier = Modifier
                .fillMaxSize(),
            onCountryClick = onCountryClick,
        )
    }
}
