package org.crossplatform.stats.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.crossplatform.stats.domain.CountryListState
import org.crossplatform.stats.ui.components.CountryList
import org.crossplatform.stats.ui.model.toCardsUI

@Composable
fun CountryListScreen(
    state: CountryListState,
    onCountryClick: (Int) -> Unit,
    modifier: Modifier = Modifier.fillMaxSize(),
) {

    Box(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            ) {
            // здесь будет еще строка поиска
            CountryList(
                countries = state.items.toCardsUI(),
                modifier = Modifier.fillMaxWidth(),
                onCountryClick = onCountryClick,
            )
        }
    }
}
