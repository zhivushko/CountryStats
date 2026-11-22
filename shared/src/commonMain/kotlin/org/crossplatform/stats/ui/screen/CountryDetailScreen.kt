package org.crossplatform.stats.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.crossplatform.stats.ui.components.CountryDetailed
import org.crossplatform.stats.ui.components.FullScreenError
import org.crossplatform.stats.ui.components.FullScreenLoading
import org.crossplatform.stats.ui.detail.CountryDetailIntent
import org.crossplatform.stats.ui.detail.CountryDetailedState
import org.jetbrains.compose.resources.stringResource

@Composable
fun CountryDetailScreen(
    state: CountryDetailedState,
    onIntent: (CountryDetailIntent) -> Unit,
    modifier: Modifier = Modifier.fillMaxSize(),
) {
    when (state) {
        CountryDetailedState.Loading -> FullScreenLoading(modifier)

        is CountryDetailedState.Error -> FullScreenError(
            message = stringResource(state.messageRes),
            onRetry = { onIntent(CountryDetailIntent.RetryRequested) },
            modifier = modifier,
        )

        is CountryDetailedState.Success -> Column(
            modifier = modifier.verticalScroll(rememberScrollState()),
        ) {
            CountryDetailed(
                country = state.country,
                neighbours = state.neighbours,
                modifier = Modifier.fillMaxSize(),
                onCountryClick = { id -> onIntent(CountryDetailIntent.NeighbourClicked(id)) },
            )
        }
    }
}
