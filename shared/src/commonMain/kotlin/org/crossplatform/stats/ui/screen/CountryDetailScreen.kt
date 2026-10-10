package org.crossplatform.stats.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.crossplatform.stats.detail.CountryDetailedState
import org.crossplatform.stats.ui.components.CountryDetailed

@Composable
fun CountryDetailScreen(
    state: CountryDetailedState,
    onCountryClick: (Int) -> Unit,
    modifier: Modifier = Modifier.fillMaxSize(),
) {
    val country = state.country
    val neighbours = state.neighbours

    //пока засовываем страну в state view model - крутим загрузку
    // потом не надо приводить country? к country, так как компилятор уже считает что country иницировали
    // технология называется smartcast


    if (country == null) {
        Box(
            modifier = modifier,
            contentAlignment = Alignment.Center,
        ) {
            CircularProgressIndicator()
        }
        return
    }
    Column(modifier =modifier.verticalScroll(rememberScrollState())) {
        CountryDetailed(
            country = country,
            neighbours = neighbours,
            modifier = Modifier
                .fillMaxSize(),
            onCountryClick = onCountryClick,
        )
    }
}
